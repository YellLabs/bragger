package com.hibu.bragger.swagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Type;

import com.hibu.bragger.wsdl.EasyWsdlHelper;

/**
 * helper to encapsulate the swagger specs version 1.1 data types and constants
 */
public class SwaggerSpecs11 {

	// see https://github.com/wordnik/swagger-core/wiki/datatypes#primitives
	private static Set<String> primitiveTypes = new HashSet<String>();
	
	// see https://github.com/wordnik/swagger-core/wiki/datatypes#containers
	private static Set<String> containerTypes = new HashSet<String>();

	// void
	private static final String DATATYPE_VOID = "void";
	
	// swagger primitives: lowercased until a swagger annotations validator is implemented
	public static final String DATATYPE_STRING = "string";
	public static final String DATATYPE_BYTE = "byte";
	public static final String DATATYPE_BOOLEAN = "boolean";
	public static final String DATATYPE_INT = "int";
	public static final String DATATYPE_LONG = "long";
	public static final String DATATYPE_FLOAT = "float";
	public static final String DATATYPE_DOUBLE = "double";
	public static final String DATATYPE_NUMBER = "number";
	public static final String DATATYPE_DATE = "date";	
	
	// containers
	public static final String DATATYPE_ARRAY = "Array";
	public static final String DATATYPE_LIST = "List";
	public static final String DATATYPE_SET = "Set";
	
	static {
		
		primitiveTypes.addAll(
			Arrays.asList(
				new String[] { 
					DATATYPE_BYTE, 
					DATATYPE_BOOLEAN, 
					DATATYPE_INT, 
					DATATYPE_LONG, 
					DATATYPE_FLOAT, 
					DATATYPE_DOUBLE, 
					DATATYPE_NUMBER, 
					DATATYPE_STRING, 
					DATATYPE_DATE // ISO-8601 Date, which is represented in a String (1970-01-01T00:00:00.000+0000)*/ 
				}
			)
		);
		
		containerTypes.addAll(
			Arrays.asList(
				new String[] {
					DATATYPE_ARRAY + "[", 
					DATATYPE_LIST + "[", 
					DATATYPE_SET + "["}
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
		
		if (isPrimitiveType(valueType) || isVoidType(valueType))
			return null;
			
		if (isContainerType(valueType)) {
			
			String itemType = "";
			
			if (valueType.startsWith("List[")) {
				itemType = (String) valueType.subSequence(5, valueType.length()-1);
			} else if (valueType.startsWith("Array[")) {
				itemType = (String) valueType.subSequence(6, valueType.length()-1);
			}
			
			if (StringUtils.isBlank(itemType) || isPrimitiveType(itemType)) {				
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
		
		if (valueType==null)
			return false;
		
		valueType = valueType.replaceAll(" ", "");
		
		return DATATYPE_VOID.equalsIgnoreCase(valueType) || "null".equalsIgnoreCase(valueType);
	}
	
	/**
	 * 
	 * @param valueType
	 * @return
	 */
	public static boolean isPrimitiveType(String valueType) {
		if (valueType==null)
			return false;
		
		valueType = valueType.replaceAll(" ", "");
		
		//TODO once a swagger annotations validator is implemented 
		//     the lowerCase is not necessary anymore
		return primitiveTypes.contains(valueType.toLowerCase()); 
	}

	/**
	 * 
	 * @param valueTypeInternal
	 * @return
	 */
	public static boolean isContainerType(String valueTypeInternal) {
		
		if (valueTypeInternal==null)
			return false;
		
		for (String type: containerTypes) {
			if (valueTypeInternal.replaceAll(" ", "").startsWith(type)) {				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * see http://www.w3.org/TR/xmlschema-2/#built-in-datatypes
	 * 
	 * @param dataType
	 * @param modelsNamespace
	 * @return
	 */
	public static Type mapBasicDataType(String dataType, String modelsNamespace, Schema typesSchema) {
		
		Type type = null;
		
		if (dataType.equals(DATATYPE_STRING)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:string");

		} else if (dataType.equals(DATATYPE_BYTE)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:byte");
			
		} else if (dataType.equals(DATATYPE_BOOLEAN)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:boolean");
			
		} else if (dataType.equals(DATATYPE_INT)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:integer");
			
		} else if (dataType.equals(DATATYPE_LONG)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:long");
	
		} else if (dataType.equals(DATATYPE_FLOAT)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:float");
			
		} else if (dataType.equals(DATATYPE_DOUBLE)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:double");

		} else if (dataType.equals(DATATYPE_NUMBER)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:double");
			
		} else if (dataType.equals(DATATYPE_DATE)) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:date");
			
		} else {
			throw new UnsupportedOperationException("the mapping from swagger primitive type " + dataType + " to an xsd one has not been implemented yet");
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
