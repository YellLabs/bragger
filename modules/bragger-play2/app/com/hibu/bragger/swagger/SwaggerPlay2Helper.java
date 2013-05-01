package com.hibu.bragger.swagger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.Play;
import play.modules.swagger.ApiHelpInventory;
import play.modules.swagger.PlayApiReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.core.util.JsonUtil;
import com.hibu.bragger.swagger.SwaggerSpecs11;

public class SwaggerPlay2Helper {
	
	/**
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Map<String, Documentation> readApiDocs() throws JsonParseException, JsonMappingException, IOException {
		
		// result
		Map<String, Documentation> docsMap = new HashMap<String, Documentation>();
		
		Map<String, Class> controllerClasses = getApiControllers();
		
		for (String resourcePath: ApiHelpInventory.getResourceNames()) {
			
			String resourceName = extractResourceName(resourcePath);
			if (resourceName!=null) {
				
				String swaggerMainUrl = com.wordnik.swagger.play.controllers.routes.ApiHelpController.getResource(resourceName).url();
				
				String json = ApiHelpInventory.getPathHelpJson(swaggerMainUrl, null);
				if (json!=null && !json.isEmpty() && controllerClasses.get(resourceName)!=null) {
					
					Documentation simpleDoc = JsonUtil.getJsonMapper().readValue(json, Documentation.class);
					
					if (controllerClasses.get(resourceName)!=null) {
						Documentation typedResourceDoc = PlayApiReader.read(controllerClasses.get(resourceName), 
								simpleDoc.apiVersion(), simpleDoc.apiVersion(), simpleDoc.basePath(), simpleDoc.resourcePath());
						docsMap.put(resourceName, typedResourceDoc);
					}
				}
			}
		}
		
		return docsMap;
	}
	
	/**
	 * 
	 * @param controllerClasses
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] getApiModelClasses() {
		
		// used a set initially to remove duplicates 
		Set<Class> modelClassesSet = new HashSet<Class>();
		
		Collection<Class> controllersClasses = getApiControllers().values();
		for (Class controllerClass : controllersClasses) {
			
			// this is used to get the internalType of the responseClasses
			Documentation typedResourceDoc = PlayApiReader.read(controllerClass, null, null, null, null);
			
			if (typedResourceDoc.getApis()!=null) {
			for (DocumentationEndPoint api : typedResourceDoc.getApis()) {
					
					if (api.getOperations()!=null) {
					for (DocumentationOperation operation : api.getOperations()) {
							
							// operation response type
							String responseModel = SwaggerSpecs11.getModelFromValueType(operation.getResponseClass());
							if (responseModel!=null) {
								String operationResponseClassName = operation.getResponseTypeInternal();
								try {									
									modelClassesSet.add(Play.application().classloader().loadClass(operationResponseClassName));
								} catch (ClassNotFoundException e) {
									Logger.error("model class not found: " + e.getMessage() + " it won't be part of the models XSD");
								}
							}
							
							// operation non primitive parameters
							if (operation.getParameters() != null) {
								for (DocumentationParameter param : operation.getParameters()) {
									String paramModel = SwaggerSpecs11.getModelFromValueType(param.getValueTypeInternal());
									if (paramModel!=null) {
										try {
											modelClassesSet.add(Play.application().classloader().loadClass(paramModel));
										} catch (ClassNotFoundException e) {
											Logger.error("model class not found: " + e.getMessage() + " it won't be part of the models XSD");
										}
									}
								}
							}
					}}
			}}
		}
		
		// convert set modelClasses to an array of Class
		return modelClassesSet.toArray(new Class[modelClassesSet.size()]);
	}


	/**
	 * @return map with entries like: "Pet" -> PetApiController.class
	 */
	public static Map<String, Class> getApiControllers() {
		
		Map<String, Class> controllerClasses = new HashMap<String, Class>();
		
		Map<String, Class<?>> resourceMap = ApiHelpInventory.getResourceMapJava();
		for (String resourcePath : resourceMap.keySet()) {
			String resourceName = extractResourceName(resourcePath);
			controllerClasses.put(resourceName, resourceMap.get(resourcePath));
		}
		
		return controllerClasses;
	}

	// ========================================================================
	
	private static String extractResourceName(String resourcePath) {
		String resourceName = null;
		int endIndex = resourcePath.lastIndexOf("/");
		if (endIndex!=-1) {
			resourceName = resourcePath.substring(endIndex+1);
		} 
		return resourceName;
	}
	
}
