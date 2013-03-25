package com.hibu.bragger.xsd;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;

/**
 * this class generates the XSDs from the API models
 * @author paolo
 */
public class ModelsXSDGenerator {

	/**
	 * TODO cache the xsd (memcached?)
	 * 
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public static String getModelsXSD(Class[] modelClasses) throws JAXBException, IOException, URISyntaxException {
		
		if (modelClasses==null || modelClasses.length==0) {
			throw new IllegalArgumentException("no model classes to generate xsd from");
		}
		
		// generating an xml schema using all the models
		JAXBContext context = JAXBContext.newInstance(modelClasses);

		// TODO find a better way to get the Schema, without writing to a file
		MySchemaOutputResolver s = new MySchemaOutputResolver();
		context.generateSchema(s);

		File f = new File(new URI(s.r.getSystemId()));

		String schemaAsString = FileUtils.readFileToString(f);
		return schemaAsString;
	}
	
	// ========================================================================
	
	/**
	 * 
	 * @author paolo
	 *
	 */
	private static class MySchemaOutputResolver extends SchemaOutputResolver {
		
		public StreamResult r = null;
		
	    public javax.xml.transform.Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
	    	r = new StreamResult(new File("/tmp/", suggestedFileName));
	    	return r;
	    }

	}

}

