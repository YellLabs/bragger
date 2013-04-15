package com.hibu.bragger.controllers;

import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.bragger.swagger.SwaggerPlay2Helper;
import com.hibu.bragger.wsdl.WSDL20gen;
import com.hibu.bragger.xsd.ModelsXSDGenerator;
import com.wordnik.swagger.core.Documentation;

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
			
			@SuppressWarnings("rawtypes")
			Class[] modelClasses = SwaggerPlay2Helper.getApiModelClasses();
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
			
			Documentation resourceDoc = SwaggerPlay2Helper.readApiDocs().get(resourceName);

			String xsdUrl = com.hibu.bragger.controllers.routes.BraggerController.getXSD().url();
			String wsdlAsString = WSDL20gen.generateWSDL20(resourceName, resourceDoc, SwaggerPlay2Helper.basicTypes, xsdUrl);

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
