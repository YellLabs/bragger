package com.hibu.bragger.wsdl;

/**
 * this class provides those constants values, related to WSDL20 generation 
 * that are used both from the server-side and client-side bragger modules
 * (namely bragger-core and bragger-client)
 * 
 * @author paolo
 *
 */
public class WSDL20GenConstants {

	// tag names
	public static final String EMPTY_RESPONSE_TYPENAME = "emptyResponse";
	public static final String ERROR_RESPONSE_TYPENAME = "errorResponse";
	public static final String ERROR_RESPONSE_ELEMENTNAME = "genericError";
	
	// namespaces
	public static final String HTTP_WWW_W3_ORG_NS_WSDL = "http://www.w3.org/ns/wsdl";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_SOAP = "http://www.w3.org/ns/wsdl/soap";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_HTTP = "http://www.w3.org/ns/wsdl/http";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS = "http://www.w3.org/ns/wsdl-extensions";
	private static final String HIBU_API_NAMESPACE = "http://hibu.com/apis";
	private static final String MODELS_NS_PREFIX = "models";

	/**
	 * 
	 * @param appName
	 * @return
	 */
	public static String getModelsNamespaceUri(String appName) {
		return HIBU_API_NAMESPACE + "/" + appName + "/" + getModelsNamespacePrefix();
	}

	/**
	 * 
	 * @param appName
	 * @param webServiceName
	 * @return
	 */
	public static String getTargetNamespaceUri(String appName, String webServiceName) {
		return HIBU_API_NAMESPACE + "/" + appName + "/" + webServiceName + "/wsdl";
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getModelsNamespacePrefix() {
		return WSDL20GenConstants.MODELS_NS_PREFIX;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getErrorResponseElementName() {
		return WSDL20GenConstants.ERROR_RESPONSE_ELEMENTNAME;
	}
	
}
