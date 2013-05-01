package com.hibu.bragger.codegen.axis2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.json.AbstractJSONDataSource;
import org.apache.axis2.json.AbstractJSONOMBuilder;
import org.apache.axis2.transport.http.util.URIEncoderDecoder;

import com.hibu.bragger.wsdl.WSDL20GenConstants;

/**
 * This is like JSONOMBuilder but it supports RESTful api methods whose response is not wrapped in a container object.
 * 
 * For example if the called RESTful api has got a getPetById method, that returns an object of type Pet, whose fieds are id and name:
 * - the wrapped response would be { "Pet" : { "id" : "1, "name" : "myPetName" } }
 * - the unwrapped response would be { "id" : "1, "name" : "myPetName" }
 * 
 * This Builder allows unmarshaling responses from methods which return unwrapped responses as well.
 * while the JSONOMBuilder shipped with axis-json only support apis which return wrapped responses.
 * 
 * @author paolo
 *
 */
public class WrappingObjectJSONOMBuilder extends AbstractJSONOMBuilder {

	private boolean jsonResponseWrappedInAContainer;
	private String serviceName;
	private String resourceName;
	
	public WrappingObjectJSONOMBuilder(String serviceName) {
		super();
		this.serviceName = serviceName;
		this.jsonResponseWrappedInAContainer = false;
		this.resourceName = "Object";
	}
	
	public WrappingObjectJSONOMBuilder(String serviceName, boolean wrappedResponse) {
		super();
		this.serviceName = serviceName;
		this.jsonResponseWrappedInAContainer = wrappedResponse;
	}

	public WrappingObjectJSONOMBuilder(String serviceName, boolean wrappedResponse, String resourceName) {
		super();
		this.serviceName = serviceName;
		this.jsonResponseWrappedInAContainer = wrappedResponse;
		this.resourceName = resourceName;
	}
	
    @Override
    protected AbstractJSONDataSource getDataSource(Reader jsonReader, String prefix, String localName) {
    	if (prefix==null || prefix.equals("")) {
    		return new WrappingObjectJSONDatasource(jsonReader, "\"" + localName + "\"", jsonResponseWrappedInAContainer);
    	} else {
    		Map<String, String> map = new HashMap<String, String>();
    		map.put(WSDL20GenConstants.getModelsNamespaceUri(serviceName), WSDL20GenConstants.getModelsNamespacePrefix());
    		return new WrappingObjectJSONDatasource(jsonReader, "\"" + prefix + "." + localName + "\"", jsonResponseWrappedInAContainer, map);
    	}
    }

    @Override
	public OMElement processDocument(InputStream inputStream, String contentType, MessageContext messageContext) throws AxisFault {
		 
		OMFactory factory = OMAbstractFactory.getOMFactory();
		String localName = "";
		String prefix = "";
		OMNamespace ns = factory.createOMNamespace("", "");

		// sets DoingREST to true because, security scenarios needs to handle in REST way
		messageContext.setDoingREST(true);

		Reader reader;

		// if the input stream is null, then check whether the HTTP method is
		// GET, if so get the
		// JSON String which is received as a parameter, and make it an input
		// stream
		if (inputStream == null) {
			EndpointReference endpointReference = messageContext.getTo();
			if (endpointReference == null) {
				throw new AxisFault("Cannot create DocumentElement without destination EPR");
			}

			String requestURL;
			try {
				requestURL = URIEncoderDecoder.decode(endpointReference.getAddress());
			} catch (UnsupportedEncodingException e) {
				throw AxisFault.makeFault(e);
			}

			String jsonString;
			int index;
			// As the message is received through GET, check for "=" sign and
			// consider the second
			// half as the incoming JSON message
			if ((index = requestURL.indexOf("=")) > 0) {
				jsonString = requestURL.substring(index + 1);
				reader = new StringReader(jsonString);
			} else {
				throw new AxisFault("No JSON message received through HTTP GET or POST");
			}
		} else {
			// Not sure where this is specified, but SOAPBuilder also determines
			// the charset
			// encoding like that
			String charSetEncoding = (String) messageContext.getProperty(Constants.Configuration.CHARACTER_SET_ENCODING);
			if (charSetEncoding == null) {
				charSetEncoding = MessageContext.DEFAULT_CHAR_SET_ENCODING;
			}
			try {
				reader = new InputStreamReader(inputStream, charSetEncoding);
			} catch (UnsupportedEncodingException ex) {
				throw AxisFault.makeFault(ex);
			}
		}

		/*
		 * Now we have to read the localname and prefix from the input stream if
		 * there is not prefix, message starts like {"foo": if there is a
		 * prefix, message starts like {"prefix:foo":
		 */
		try {
			
			if (jsonResponseWrappedInAContainer) {
				// read the stream until we find a : symbol
				char temp = (char) reader.read();
				while (temp != ':') {
					if (temp != ' ' && temp != '{' && temp != '\n' && temp != '\r' && temp != '\t') {
						localName += temp;
					}
					temp = (char) reader.read();
				}

				// if the part we read ends with ", there is no prefix, otherwise it has a prefix
				if (localName.charAt(0) == '"') {
					if (localName.charAt(localName.length() - 1) == '"') {
						localName = localName.substring(1, localName.length() - 1);
					} else {
						prefix = localName.substring(1, localName.length()) + ":";
						localName = "";
						// so far we have read only the prefix, now lets read the localname
						temp = (char) reader.read();
						while (temp != ':') {
							if (temp != ' ') {
								localName += temp;
							}
							temp = (char) reader.read();
						}
						localName = localName.substring(0, localName.length() - 1);
					}
				}
			} else {
				if (messageContext.isProcessingFault()) {
					ns = factory.createOMNamespace(WSDL20GenConstants.getModelsNamespaceUri(serviceName), WSDL20GenConstants.getModelsNamespacePrefix());
					prefix = WSDL20GenConstants.getModelsNamespacePrefix();
					localName = WSDL20GenConstants.getErrorResponseElementName();
				}
				else {					
					localName = resourceName;
				}
			}
		} catch (IOException e) {
			throw AxisFault.makeFault(e);
		}
		
		AbstractJSONDataSource jsonDataSource = getDataSource(reader, prefix, localName);
		return factory.createOMElement(jsonDataSource, localName, ns);
	}
	
}
