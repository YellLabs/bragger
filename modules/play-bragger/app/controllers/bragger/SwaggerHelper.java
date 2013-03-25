package controllers.bragger;

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

import play.modules.swagger.ApiHelpInventory;
import play.modules.swagger.PlayApiReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.core.util.JsonUtil;

public class SwaggerHelper {
	
	public static Set<String> basicTypes = new HashSet<String>();
	
	public static Map<String, Class> controllerClasses = new HashMap<String, Class>();
	
	static {
		
		basicTypes.addAll(Arrays.asList(new String[]{"string", "String", "number", "int", "boolean", "object", "Array", "void", "null", "any"}));
		
		// TODO get the list of controller classes dynamically or at least from the configs
//		try {
//			controllerClasses.put("pet", Class.forName(PetApiController.class.getName()));
//			controllerClasses.put("user", Class.forName(UserApiController.class.getName()));
//			controllerClasses.put("store", Class.forName(StoreApiController.class.getName()));
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
		
	}
	
	/**
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Map<String, com.wordnik.swagger.core.Documentation> readApiDocs() throws JsonParseException, JsonMappingException, IOException {
		
		// result
		Map<String, com.wordnik.swagger.core.Documentation> docsMap = new HashMap<String, com.wordnik.swagger.core.Documentation>();
		
		// call swagger code to get the main resources
		String resourcesResponseAsString = ApiHelpInventory.getRootHelpJson(null);
		
		List<String> resourcesNames = new ArrayList<String>();
		com.wordnik.swagger.core.Documentation d1 = JsonUtil.getJsonMapper().readValue(resourcesResponseAsString, com.wordnik.swagger.core.Documentation.class);
		for (DocumentationEndPoint api: d1.getApis()) {				
			String path = api.getPath();
			int endIndex = path.lastIndexOf("/");
			if (endIndex!=-1) {
				path = path.substring(endIndex+1);
			}
			path = path.replace(".{format}", "");
			resourcesNames.add(path);
		}
		
		for (String resourceName: resourcesNames) {
			
			//Promise<Response> promise = WS.url("http://localhost:9000/api-docs.json/"+resourceName).get(); // TODO at least use reverse routing
			//String json = promise.get().asJson().toString();
			//com.wordnik.swagger.core.Documentation d2 = JsonUtil.getJsonMapper().readValue(json, com.wordnik.swagger.core.Documentation.class);
			//docsMap.put(resourceName, d2);
			
			String json = ApiHelpInventory.getPathHelpJson("/"+resourceName);
			if (json!=null && !json.isEmpty() && controllerClasses.get(resourceName)!=null) {
				
				com.wordnik.swagger.core.Documentation simpleDoc = 
						JsonUtil.getJsonMapper().readValue(json, com.wordnik.swagger.core.Documentation.class);
				
				com.wordnik.swagger.core.Documentation typedResourceDoc = 
						PlayApiReader.read(controllerClasses.get(resourceName), simpleDoc.apiVersion(), simpleDoc.apiVersion(), simpleDoc.basePath(), simpleDoc.resourcePath()); 

				docsMap.put(resourceName, typedResourceDoc);
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
	public static Class[] getApiModelClasses(Collection<Class> controllerClasses) {
		
		// used a set initially to remove duplicates 
		Set<Class> modelClassesSet = new HashSet<Class>();
		
		for (Class controllerClass : controllerClasses) {
			
			// this is used to get the internalType of the responseClasses
			// TODO this should be given as an external dependency (maybe a Map<Class,Documentation> which maps a controller class to the corresponding swagger doc), 
			// it would allow this method to become
			// independent of the PlayReader which means depending only on the swagger-core module rather than depending from the play2-swagger.
			// this would in turn mean being independent from play framework and then this method could (and should!) be moved to the bragger-core java module
			com.wordnik.swagger.core.Documentation typedResourceDoc = PlayApiReader.read(controllerClass, null, null, null, null);

			for (DocumentationEndPoint api : typedResourceDoc.getApis()) {
				for (DocumentationOperation operation : api.getOperations()) {
					
					try {
						
						// operation response type
						if (!SwaggerHelper.basicTypes.contains(operation.getResponseClass())) {
							String operationResponseClassName = operation.getResponseTypeInternal();
								modelClassesSet.add(Class.forName(operationResponseClassName));
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
						// TODO use plain logback to remove dependency from Play
						//Logger.error("class not found: " + e.getMessage());
					}
				}
			}
		}
		
		
		// convert set modelClasses to an array of Class
		return modelClassesSet.toArray(new Class[modelClassesSet.size()]);
	}
	
}
