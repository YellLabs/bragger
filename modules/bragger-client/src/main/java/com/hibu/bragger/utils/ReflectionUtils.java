package com.hibu.bragger.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paolo
 */
public class ReflectionUtils {
	
	private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class.getName());
	
	/**
	 * this method scans tail-recursively the list of classes provided
	 * and returns the names of the non scalar fields, like arrays, Iterables, enums. 
	 * 
	 * TODO what about name clashes??
	 * If one of the classes C1 has a non-array element called "elem1" 
	 * and another class C2 has a list called "elem1", 
	 * then C1.elem1 would be rendered as an array which would be wrong!!
	 * unlikely but possible
	 */
	public static Set<String> findMultipleValueFields(Class... classes) {
		
		Set<String> arraysOfPrimitivesNames = new HashSet<String>();
		
		findArraysOfPrimitivesTailRecursive(arraysOfPrimitivesNames, classes);
		
		return arraysOfPrimitivesNames;
	}
	
	// ========================================================================
	
	/**
	 * is the java compiler able to optimize for tail recursive functions??????
	 * @param classes
	 * @param accumulator
	 */
    private static void findArraysOfPrimitivesTailRecursive(Set<String> accumulator, Class... classes) {
    	
		for (Class clazz : classes) {
			
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				
				Class<?> type = field.getType();
				
				// arrays
				if (type.isArray()) {
					Class arrayComponentType = type.getComponentType();
					if (arrayComponentType.isPrimitive() || String.class.isAssignableFrom(arrayComponentType)) {
						addNameToAccumulator(accumulator, clazz, field, classes);
					}
				} 
				// lists, collections, sets, ...
				else if (Iterable.class.isAssignableFrom(type)) {
					addNameToAccumulator(accumulator, clazz, field, classes);
					
					Class genericType = getGenericType(field);
					if (genericType.isPrimitive() || String.class.isAssignableFrom(genericType)) {
						findArraysOfPrimitivesTailRecursive(accumulator, genericType);
					}
				}
				// enums
				else if (type.isEnum()) {
					// TODO implement				
				}
				// non String objects
				else if (!type.isPrimitive() && !String.class.isAssignableFrom(type)) {
					// embedded entity: tail recursive call
					findArraysOfPrimitivesTailRecursive(accumulator, type);
				}
			}
		}
	}

    /**
     * If one of the classes C1 has a non-array element called "elem1" 
     * and another class C2 has a list called "elem1", 
     * then C1.elem1 would be rendered as an array which would be wrong!! unlikely but possible
     * for the time being let's just print a warning
     * 
     * @param accumulator
     * @param clazz
     * @param field
     * @param classes
     */
	private static void addNameToAccumulator(Set<String> accumulator, Class clazz, Field field, Class... classes) {
		if (accumulator.contains(field.getName())) {
			logger.warn("findArraysOfPrimitives: the array of primitives " + clazz.getSimpleName() + "." + field.getName() +
					" may have been confused with a homonymous non-array field contained directly or recursively" +
					" in one of the following classes (" + classes + ")");
		}
		accumulator.add(field.getName());
	}

	private static Class getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if(genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            return (Class) parameterizedType.getActualTypeArguments()[0];
        }
        return null;
    }

}
