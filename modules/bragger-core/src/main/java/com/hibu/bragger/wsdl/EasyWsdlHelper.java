package com.hibu.bragger.wsdl;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.SchemaFactory;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.SimpleType;

public class EasyWsdlHelper {

	public static SimpleType getSimpleType(String namespace, String name) {
		SimpleType simpleType = null;
		try {
			Schema newSchema = SchemaFactory.newInstance().newSchema();
			newSchema.setTargetNamespace(namespace);
			simpleType = (SimpleType) newSchema.createSimpleType();
			simpleType.setQName(new QName(name));
			
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		
		return simpleType;	
	}

	public static ComplexType getComplexType(String namespace, String name) {
		ComplexType responseMessageType = null;
		try {
			Schema newSchema = SchemaFactory.newInstance().newSchema();
			newSchema.setTargetNamespace(namespace);
			responseMessageType = (ComplexType) newSchema.createComplexType();
			responseMessageType.setQName(new QName(name));
			
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		
		return responseMessageType;	
	}
	
	public static Element getElement(String namespace, String name) {
		try {
			Schema newSchema = SchemaFactory.newInstance().newSchema();
			newSchema.setTargetNamespace(namespace);
			Element element = newSchema.createElement();
			element.setQName(new QName(namespace, name));
			return element;
		} catch (SchemaException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
