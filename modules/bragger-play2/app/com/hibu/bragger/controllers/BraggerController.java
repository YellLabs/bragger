package com.hibu.bragger.controllers;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.bragger.swagger.SwaggerPlay2Helper;
import com.hibu.bragger.wsdl.WSDL20gen;
import com.hibu.bragger.wsdl.Wsdl20Validator;
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
			
			Logger.info(me() + "generating xsd for models: " + models(modelClasses));
			
			String schemaAsString = ModelsXSDGenerator.getModelsXSD(modelClasses);
			
			Logger.info(me() + "xsd generated, returning...");
			
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
			
			String appName = Play.application().configuration().getString("application.name");
			
			String wsdlAsString = WSDL20gen.generateWSDL20(appName, resourceName, resourceDoc, xsdUrl);
			
//			Wsdl20Validator validator = new Wsdl20Validator(wsdlAsString);
//			FIXME: always invalid
//			if (!validator.isValid()) {
//				Logger.error("the generated wsdl is not valid");
//			}
			
			return ok(wsdlAsString).as("text/xml");
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}
	
	private static String models(Class[] modelClasses) {
		String models = "";
		
		if (modelClasses!=null) {				
			for (Class c: modelClasses)
				models = c.getName() + ", " + models;
		}
		if (models.endsWith(", ")) 
			models = models.substring(0, models.length()-2);
		
		return models;
	}
	
	private static String me() { return "[" + BraggerController.class.getSimpleName() + "] "; }
	
}
