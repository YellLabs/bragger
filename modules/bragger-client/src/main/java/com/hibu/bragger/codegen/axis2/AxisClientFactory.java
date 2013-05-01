package com.hibu.bragger.codegen.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.MessageFormatter;

import com.hibu.bragger.codegen.ClientFactory;
import com.hibu.bragger.utils.ReflectionUtils;

/**
 * Provides factory methods for the api stub auto generated with axis tool wsdl2java
 * 
 * @author paolo
 */
public class AxisClientFactory implements ClientFactory {
	
	/**
	 * This method instantiate and configure an api client using axis stub, based on the types provided. 
	 * 
	 * When the last parameter wrappedResponse is true, the client expects the called api returns the response objects
	 * wrapped in a container, see <code>wrappedResponse</code>
	 * 
	 * @param serviceInterface
	 * @param stubImplementation
	 * @param apiResourceClass
	 * @param wrappedResponse </br>
	 * For example, if the called RESTful api has got a getResourceById method that returns an resource of type Resource, whose fields are id and name, then:<br/>
	 * <ul>
	 * <li>- the wrapped response would be <code>{ "ResourceContainer" : { "id" :"1, "name" : "myPetName" } }</code></li>
	 * <li>- the unwrapped response would be <code>{ "id" : "1, "name" : "myPetName" }</code></li>
	 * </ul></br> 
	 * defaults to false
	 * @return new client for the api
	 * @throws AxisFault
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public <ITF, IMPL, RSC> ITF getClient(Class<ITF> serviceInterface, Class<IMPL> stubImplementation, Class<RSC> apiResourceClass, boolean wrappedResponse, String serviceAppName) throws InstantiationException {
		
		try {
			
			Stub stub = (Stub) stubImplementation.newInstance();
			
			// using a custom builder to unmarshal responses from the api
			Builder jsonUnmarshaller = new WrappingObjectJSONOMBuilder(serviceAppName, wrappedResponse, apiResourceClass.getSimpleName());
			
			// using a custom jsonformatter to marshal objects to send them to the api on the wire
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

		} catch (Exception e) {
			throw new InstantiationException("caused by " + e.getClass().getName() + ": " +e.getMessage());
		}
	}
	
	/**
	 * this method instantiate and configure an api client using axis stub, based on the types provided</br>
	 * </p>
	 * the created client will support only non wrapped responses, for an explanation of wrapped response see {@link #getClient(Class, Class, Class, boolean) newClient}</br>
	 * 
	 * @param serviceInterface
	 * @param stubImplementation
	 * @param apiResourceClass
	 * @return new client for the api
	 * @throws AxisFault
	 * @throws InstantiationException
	 */
	public <ITF, IMPL, RSC> ITF getClient(Class<ITF> serviceInterface, Class<IMPL> stubImplementation, Class<RSC> apiResourceClass, String serviceAppName)  throws InstantiationException {
		return getClient(serviceInterface, stubImplementation, apiResourceClass, false, serviceAppName);
	}
	
	/**
	 * This method instantiate and configure an api client using axis stub, based on the types provided</br>
	 * it also finds dynamically an implementation of the service interface, that's why this method is expensive</br> 
	 * so the object returned is meant to be kept on the client class and reused as much as possible</br>
	 * </p>
	 * the created client will support only non wrapped responses, for an explanation of wrapped response see {@link #getClient(Class, Class, Class, boolean) newClient}</br> 
	 * 
	 * @param serviceInterface
	 * @param apiResource
	 * @return an implementation of the interface provided at <code>apiInterface</code>
	 * @throws InstantiationException 
	 * @throws AxisFault 
	 */
	public <ITF, RSC> ITF getClient(Class<ITF> serviceInterface, Class<RSC> apiResourceClass) throws InstantiationException {
		
		// this is the expensive call
		Class<Stub> stubImpl = findStubImplementationForInterface(serviceInterface);
		
		return getClient(serviceInterface, stubImpl, apiResourceClass, null);
	}
	
	// ========================================================================
	
	private <ITF> Class<Stub> findStubImplementationForInterface(Class<ITF> serviceInterface) {
		// TODO implement see http://stackoverflow.com/questions/347248/how-can-i-get-a-list-of-all-the-implementations-of-an-interface-programmatically
		// use http://docs.oracle.com/javase/1.4.2/docs/guide/jar/jar.html#Service%20Provider 
		// or https://code.google.com/p/reflections/
		return null;
	}
		
}
