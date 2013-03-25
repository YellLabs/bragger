package com.hibu.bragger.wsdl;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.SchemaFactory;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.SimpleType;

public class EasyWsdlHelper {

	public static SimpleType getSimpleType(String namespace, String name) {
		SimpleType responseMessageType = null;
		try {
			Schema newSchema = SchemaFactory.newInstance().newSchema();
			newSchema.setTargetNamespace(namespace);
			responseMessageType = (SimpleType) newSchema.createSimpleType();
			responseMessageType.setQName(new QName(name));
			
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		
		return responseMessageType;	
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
	
}
