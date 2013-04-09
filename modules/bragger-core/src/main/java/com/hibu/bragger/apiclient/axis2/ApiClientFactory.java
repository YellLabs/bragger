package com.hibu.bragger.apiclient.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Stub;
import org.apache.axis2.json.JSONOMBuilder;
import org.apache.axis2.transport.MessageFormatter;

import com.hibu.bragger.utils.ReflectionUtils;

/**
 * provides factory methods for the api stub auto generated with axis tool wsdl2java
 * 
 * @author paolo
 *
 */
public class ApiClientFactory {

	/**
	 * this method instantiate and configure an api client using axis stub, based on the types provided
	 *  
	 * @param serviceInterface
	 * @param stubImplementation
	 * @param apiResourceClass
	 * @return
	 * @throws AxisFault
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <ITF, IMPL, RSC> ITF newClient(Class<ITF> serviceInterface, Class<IMPL> stubImplementation, Class<RSC> apiResourceClass) 
			throws AxisFault, InstantiationException {
		
		try {
			
			Stub stub = (Stub) stubImplementation.newInstance();
			
			// using the ombuilder from the package axis2-json
			JSONOMBuilder jsonUnmarshaller = new JSONOMBuilder();
			
			// using a custom jsonformatter
			MessageFormatter jsonMarshaller = null;
			if (apiResourceClass==null)
				jsonMarshaller = new NSIgnoreJSONFormatter(ReflectionUtils.findMultipleValueFields());
			else
				jsonMarshaller = new NSIgnoreJSONFormatter(ReflectionUtils.findMultipleValueFields(apiResourceClass));
			
			// mapping the marshaller/unmarshaller to the json content type
			// the content type used is specified in the WSDL, in the binding section
			// see the wsdl-http:inputSerialization and wsdl-http:outputSerialization attributes
			stub._getServiceClient().getAxisConfiguration().addMessageFormatter("application/json", jsonMarshaller);
			stub._getServiceClient().getAxisConfiguration().addMessageBuilder("application/json", jsonUnmarshaller);
			
			return (ITF) stub;

		} catch (IllegalAccessException e) {
			throw new InstantiationException("caused by " + e.getClass().getName() + ": " +e.getMessage());
		}
	}
	
	/**
	 * this method instantiate and configure an api client using axis stub, based on the types provided
	 * it also finds dynamically an implementation of the service interface, that's why this method is expensive 
	 * so the object returned is meant to be kept on the client class and reused as much as possible
	 * 
	 * @param apiInterface
	 * @param apiResource
	 * @return an implementation of apiInterface
	 * @throws InstantiationException 
	 * @throws AxisFault 
	 */
	public static <ITF, RSC> ITF newClient(Class<ITF> apiInterface, Class<RSC> apiResourceClass) 
			throws AxisFault, InstantiationException {
		
		// this is the expensive call
		Class<Stub> stubImpl = findStubImplementationForInterface(apiInterface);
		
		return newClient(apiInterface, stubImpl, apiResourceClass);
	}
	
	private static <ITF> Class<Stub> findStubImplementationForInterface(Class<ITF> serviceInterface) {
		// TODO implement see http://stackoverflow.com/questions/347248/how-can-i-get-a-list-of-all-the-implementations-of-an-interface-programmatically
		// use http://docs.oracle.com/javase/1.4.2/docs/guide/jar/jar.html#Service%20Provider 
		// or https://code.google.com/p/reflections/
		return null;
	}
		
}
