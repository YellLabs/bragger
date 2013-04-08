package com.hibu.bragger.swagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class SwaggerHelper {
	
	private static Logger logger = LoggerFactory.getLogger(SwaggerHelper.class.getName());
	
	public static Set<String> basicTypes = new HashSet<String>();
	
	static {
		basicTypes.addAll(Arrays.asList(new String[]{"string", "String", "number", "int", "boolean", "object", "Array", "void", "null", "any"}));
	}
		
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
				
				String json = ApiHelpInventory.getPathHelpJson("/api-docs.json/"+resourceName, null);
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
		
		for (Class controllerClass : getApiControllers().values()) {
			
			// this is used to get the internalType of the responseClasses
			Documentation typedResourceDoc = PlayApiReader.read(controllerClass, null, null, null, null);

			for (DocumentationEndPoint api : typedResourceDoc.getApis()) {
				for (DocumentationOperation operation : api.getOperations()) {
					
					try {
						
						// operation response type
						if (!SwaggerHelper.basicTypes.contains(operation.getResponseClass())) {
							String operationResponseClassName = operation.getResponseTypeInternal();
							modelClassesSet.add(Play.application().classloader().loadClass(operationResponseClassName));
						}
						
						// operation non primitive parameters
						if (operation.getParameters() != null) {
							for (DocumentationParameter param : operation.getParameters()) {
								if (StringUtils.isNotBlank(param.getValueTypeInternal()))
									if (!SwaggerHelper.basicTypes.contains(operation.getResponseClass())) {
										String paramInternalType = param.getParamType();
										modelClassesSet.add(Class.forName(paramInternalType));
									}
							}
						}
						
					} catch (ClassNotFoundException e) {
						logger.error("model class not found: " + e.getMessage());
					}
				}
			}
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
