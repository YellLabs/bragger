package com.hibu.bragger.codegen;

/**
 * This interface defines how to get an instance of an auto generated api client stub.
 *  
 * Bragger should be able to generate api stubs using different frameworks.
 * It supports for now only clients generated via axis2's wsdl2java tool.
 * 
 * 
 * @author paolo
 */
public interface ClientFactory {

	public <ITF, IMPL, RSC> ITF newClient(Class<ITF> serviceInterface, Class<IMPL> stubImplementation, Class<RSC> apiResourceClass, boolean wrappedResponse) throws InstantiationException;

	public <ITF, IMPL, RSC> ITF getClient(Class<ITF> serviceInterface, Class<IMPL> stubImplementation, Class<RSC> apiResourceClass)  throws InstantiationException;
	
}
