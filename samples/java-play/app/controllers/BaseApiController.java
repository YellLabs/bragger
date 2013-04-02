package controllers;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import play.mvc.Controller;

import com.wordnik.swagger.sample.data.PetData;
import com.wordnik.swagger.sample.model.Category;
import com.wordnik.swagger.sample.model.Order;
import com.wordnik.swagger.sample.model.Pet;
import com.wordnik.swagger.sample.model.Tag;
import com.wordnik.swagger.sample.model.User;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

public class BaseApiController extends Controller {

	private static JAXBContext jaxbContext = null;
		
	protected static boolean returnXml() {
		return request.path.contains(".xml");
	}

	protected static String marshallToXml(Object o) {
		
		StringWriter stringWriter = new StringWriter();
		try {
			
			if (jaxbContext==null)
				jaxbContext = JAXBContext.newInstance(Order.class, Category.class, Pet.class, Tag.class, User.class);
			
			jaxbContext.createMarshaller().marshal(o, stringWriter);
			
		} catch (JAXBException e) { }
		
		return stringWriter.toString();
	}
	  
}