package com.hibu.bragger.wsdl;

/**
 *  this class provides constants related to WSDL20 generation

 * @author paolo
 *
 */
public class WSDL20GenConstants {

	public static final String ERROR_RESPONSE_TYPENAME = "errorResponse";
	public static final String ERROR_RESPONSE_ELEMENTNAME = "genericError";
	public static final String EMPTY_RESPONSE_TYPENAME = "EmptyResponse";
	
	public static final String HTTP_WWW_W3_ORG_NS_WSDL = "http://www.w3.org/ns/wsdl";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_SOAP = "http://www.w3.org/ns/wsdl/soap";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_HTTP = "http://www.w3.org/ns/wsdl/http";
	public static final String HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS = "http://www.w3.org/ns/wsdl-extensions";

	public static final String BINDING_PROTOCOL_HTTP = "http";
	public static final String WSDL20_DOCUMENT_NAME_SUFFIX = "_wsdl20_document";
	public static final String INTERFACE_NAME_SUFFIX = "interface";
	public static final String SERVICE_NAME_SUFFIX = "service";
	public static final String REST_ENDPOINT_SUFFIX = "rest_endpoint";
	public static final String REST_BINDING_SUFFIX = "rest_binding";
	
	private static final String HTTP_HIBU_COM_API = "http://hibu.com/apis";
	private static final String TYPES = "models";

	/**
	 * 
	 * @param appName
	 * @return
	 */
	public static String getModelsNamespaceUri(String appName) {
		return HTTP_HIBU_COM_API + "/" + appName + "/" + getModelsNamespacePrefix();
	}

	/**
	 * 
	 * @param appName
	 * @param webServiceName
	 * @return
	 */
	public static String getTargetNamespaceUri(String appName, String webServiceName) {
		return HTTP_HIBU_COM_API + "/" + appName + "/" + webServiceName + "/wsdl";
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getModelsNamespacePrefix() {
		return WSDL20GenConstants.TYPES;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getErrorResponseElementName() {
		return WSDL20GenConstants.ERROR_RESPONSE_ELEMENTNAME;
	}
	
}
