package controllers.bragger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import com.wordnik.swagger.core.Documentation;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Router;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
	public static void getXSD() {
		
		try {
			
			// getting the models used in the operation of the passed controllers
			@SuppressWarnings("rawtypes")
			Class[] modelClasses = SwaggerHelper.getApiModelClasses();
			
			Logger.info(me() + "generating xsd for models: " + models(modelClasses));
			
			String schemaAsString = ModelsXSDGenerator.getModelsXSD(modelClasses);
			
			Logger.info(me() + "xsd generated, returning...");
			
			renderXml(schemaAsString);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			error(500, e.getMessage());
		}
	}
	
	/**
	 * generates the wsdl from the application interface
	 * @param resourceName
	 * @return
	 */
	public static void getWSDL(String resourceName) {
		
		try {
			
			Documentation resourceDoc = SwaggerHelper.readApiDocs().get(resourceName);
			
			String xsdUrl = Router.reverse("controllers.controllers.bragger.BraggerController.getXSD").url;
					
			String appName = Play.configuration.getProperty("application.name");
			
			if (appName==null || appName.isEmpty())
				throw new Exception("wsdl cannot be generated when no application.name is configured in the config file");
			
			String wsdlAsString = WSDL20gen.generateWSDL20(appName, resourceName, resourceDoc, xsdUrl);
			
			renderXml(wsdlAsString);
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			error(500, e.getMessage());
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
