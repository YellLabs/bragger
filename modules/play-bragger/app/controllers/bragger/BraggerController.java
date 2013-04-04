package controllers.bragger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import com.wordnik.swagger.core.Documentation;
import play.mvc.Controller;

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
			String schemaAsString = ModelsXSDGenerator.getModelsXSD(modelClasses);
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
			String wsdlAsString = WSDL20gen.generateWSDL20(resourceName, resourceDoc, SwaggerHelper.basicTypes);
			renderXml(wsdlAsString);
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			notFound(e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			error(500, e.getMessage());
		}
	}
		
}
