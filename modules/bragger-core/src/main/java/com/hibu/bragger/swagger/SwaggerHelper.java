package com.hibu.bragger.swagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Type;

import com.hibu.bragger.wsdl.EasyWsdlHelper;

public class SwaggerHelper {

	private static Set<String> basicTypes = new HashSet<String>();
	
	private static Set<String> arrayTypes = new HashSet<String>();
	
	static {
		
		basicTypes.addAll(
			Arrays.asList(
				new String[]{"string", "String", "number", "int", "boolean", "object", "any"}
			)
		);
		
		arrayTypes.addAll(
			Arrays.asList(
				new String[]{"Array[", "List["}
			)
		);
	}
	
	/**
	 * 
	 * @param valueType
	 * @return
	 */
	public static String getModelFromValueType(String valueType) {
		
		if (StringUtils.isBlank(valueType)) 
			return null;
	
		valueType = valueType.replaceAll(" ", "");
		
		if (isBasicType(valueType) || isVoidType(valueType))
			return null;
			
		if (isArrayType(valueType)) {
			
			String itemType = "";
			
			if (valueType.startsWith("List[")) {
				itemType = (String) valueType.subSequence(5, valueType.length()-1);
			} else if (valueType.startsWith("Array[")) {
				itemType = (String) valueType.subSequence(6, valueType.length()-1);
			}
			
			if (StringUtils.isBlank(itemType) || isBasicType(itemType)) {				
				return null;
			}
			else {				
				return itemType;
			}
			
		} else {
			return valueType;
		}
	}

	/**
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getModelSimpleName(String dataType) {
		
		String modelCompleteName = getModelFromValueType(dataType);
		
		if (modelCompleteName==null)
			return null;
		else
			return modelCompleteName.substring(modelCompleteName.lastIndexOf(".")+1);
	}

	/**
	 * 
	 * @param valueType
	 * @return
	 */
	public static boolean isVoidType(String valueType) {
		
		if (valueType==null || valueType=="")
			return true;
		
		valueType = valueType.replaceAll(" ", "");
		
		return "void".equalsIgnoreCase(valueType) || "null".equalsIgnoreCase(valueType);
	}
	
	/**
	 * 
	 * @param valueType
	 * @return
	 */
	public static boolean isBasicType(String valueType) {
		if (valueType==null)
			return false;
		
		valueType = valueType.replaceAll(" ", "");
		
		return basicTypes.contains(valueType);
	}

	/**
	 * 
	 * @param valueTypeInternal
	 * @return
	 */
	public static boolean isArrayType(String valueTypeInternal) {
		
		if (valueTypeInternal==null)
			return false;
		
		for (String type: arrayTypes) {
			if (valueTypeInternal.replaceAll(" ", "").startsWith(type)) {				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @param dataType
	 * @param modelsNamespace
	 * @return
	 */
	public static Type mapBasicDataType(String dataType, String modelsNamespace, Schema typesSchema) {
		
		Type type = null;
		
		if (dataType.equalsIgnoreCase("void")) {
			type = EasyWsdlHelper.getComplexType(modelsNamespace, dataType);
			ComplexType ctype = (ComplexType) type;
			ctype.setSequence(ctype.createSequence());
			
			// add this type definition to the types section of the wsdl document
			if (typesSchema.getType(type.getQName())==null) {
				System.out.println("adding type " + type.getQName() + " to schema");
				typesSchema.addType(type);
			}
		}
		
		if (dataType.equalsIgnoreCase("string")) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:string");
		}
		
		return type;
	}

	/**
	 * 
	 * @param responseClass
	 * @param modelsNamespace
	 * @param typesSchema
	 * @return
	 */
	public static Type mapVoidDataType(String withName, String modelsNamespace, Schema typesSchema) {
		
		Type type = EasyWsdlHelper.getComplexType(modelsNamespace, withName);
		ComplexType ctype = (ComplexType) type;
		ctype.setSequence(ctype.createSequence());
			
		// add this type definition to the types section of the wsdl document
		if (typesSchema.getType(type.getQName())==null) {
			System.out.println("adding type " + type.getQName() + " to schema");
			typesSchema.addType(type);
		}
		
		return type;
	}
	
}
