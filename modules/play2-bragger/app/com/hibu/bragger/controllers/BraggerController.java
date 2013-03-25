package com.hibu.bragger.controllers;

import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.bragger.helpers.SwaggerHelper;
import com.hibu.bragger.wsdl.WSDL20gen;
import com.hibu.bragger.xsd.ModelsXSDGenerator;

/**
 * 
 * @author paolo
 *
 */
public class BraggerController extends Controller {
	
	/**
	 * generates the xml schemas for the app models
	 * @return
	 */
	public static Result getXSD() {
		
		try {
			
			// getting the models used in the operation of the passed controllers
			@SuppressWarnings("rawtypes")
			Class[] modelClasses = SwaggerHelper.getApiModelClasses(SwaggerHelper.controllerClasses.values());
			
			String schemaAsString = ModelsXSDGenerator.getModelsXSD(modelClasses);
			return ok(schemaAsString).as("application/xml");
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}
	
	/**
	 * generates the wsdl from the application interface
	 * @param resourceName
	 * @return
	 */
	public static Result getWSDL(String resourceName) {
		
		try {
			
			// input data from swagger
			Map<String, com.wordnik.swagger.core.Documentation> docsMap = SwaggerHelper.readApiDocs();
			
			com.wordnik.swagger.core.Documentation resourceDoc = docsMap.get(resourceName);
			
			String wsdlAsString = WSDL20gen.generateWSDL20(resourceName, resourceDoc, SwaggerHelper.basicTypes);
			return ok(wsdlAsString).as("text/xml");
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}
		
}
