package com.hibu.bragger.wsdl;

import java.net.URI;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Include;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Input;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Output;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.Types;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfBinding.BindingConstants;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfDescription.WSDLVersionConstants;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfOperation.MEPPatternConstants;
import org.ow2.easywsdl.wsdl.impl.wsdl20.BindingOperationImpl;
import org.ow2.easywsdl.wsdl.impl.wsdl20.WSDLWriterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

/**
 *  this class generates a WSDL20 document form API metadata information
 *  provided by swagger
 *  
 *  example of generating client stub code using axis2
 *  wsdl2java.sh -wv 2.0 --databinding-method jaxbri -p com.hibu.api.petservice -u --serverside-interface --unpack-classes --test-case --over-ride -uri http://localhost:9000/api-docs.wsdl/pet
 *  
 * @author paolo
 *
 */
public class WSDL20gen {
	
	private static Logger logger = LoggerFactory.getLogger(WSDL20gen.class.getName());
	
	private static final String HTTP_WWW_W3_ORG_NS_WSDL = "http://www.w3.org/ns/wsdl";
	private static final String HTTP_WWW_W3_ORG_NS_WSDL_SOAP = "http://www.w3.org/ns/wsdl/soap";
	private static final String HTTP_WWW_W3_ORG_NS_WSDL_HTTP = "http://www.w3.org/ns/wsdl/http";
	private static final String HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS = "http://www.w3.org/ns/wsdl-extensions";

	private static final String BINDING_PROTOCOL_HTTP = "http";
	private static final String WSDL20_DOCUMENT_NAME_SUFFIX = "_wsdl20_document";
	private static final String INTERFACE_NAME_SUFFIX = "interface";
	private static final String SERVICE_NAME_SUFFIX = "service";
	private static final String REST_ENDPOINT_SUFFIX = "rest_endpoint";
	private static final String REST_BINDING_SUFFIX = "rest_binding";
	private static final String HTTP_HIBU_COM_API = "http://hibu.com/api";
	private static final String TYPES = "models";

	/**
	 * 
	 * @param resourceName
	 * @return
	 * @throws BraggerException
	 */
	public static String generateWSDL20(String resourceName, Documentation resourceDoc, Set<String> basicTypes, String xsdUrl) {
		
		if (resourceDoc==null) {
			throw new IllegalArgumentException("resourceName=" + resourceName + " not found");
		}

		try {
			
			String webServiceName = resourceName + SERVICE_NAME_SUFFIX;
			String descriptionName = webServiceName + WSDL20_DOCUMENT_NAME_SUFFIX;
			
			String interfaceName = webServiceName + INTERFACE_NAME_SUFFIX; 
			String endpointName = webServiceName + REST_ENDPOINT_SUFFIX;
			String restHttpBindingName = interfaceName + REST_BINDING_SUFFIX;

			String mainServiceNamespace = HTTP_HIBU_COM_API + "/" + webServiceName;
			String targetNamespace = mainServiceNamespace + "/wsdl";
			String modelsNamespace = mainServiceNamespace + "/" + WSDL20gen.TYPES;
			
			String bindingProtocolHttp = BINDING_PROTOCOL_HTTP;
	
			// global infos
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			Description desc20 = wsdlFactory.newDescription(WSDLVersionConstants.WSDL20);
			desc20.setTargetNamespace(targetNamespace);
			desc20.setQName(new QName(desc20.getTargetNamespace(), descriptionName));
	
			// types
			Types types = desc20.createTypes();

			// first schema is only to include the types generated via JAXB from models
			Schema modelsSchema = types.createSchema();
			modelsSchema.setTargetNamespace(modelsNamespace);
			Include includeDef = modelsSchema.createInclude();
			includeDef.setLocationURI(new URI(resourceDoc.getBasePath() + xsdUrl));
			modelsSchema.addInclude(includeDef);
			types.addSchema(modelsSchema);

			desc20.setTypes(types);
			
			// interface
			InterfaceType itf = desc20.createInterface();
			itf.setQName(new QName(desc20.getTargetNamespace(), interfaceName));
			desc20.addInterface(itf);
	
			// binding
			Binding binding = desc20.createBinding();
			binding.setQName(new QName(desc20.getTargetNamespace(), restHttpBindingName));
			binding.setInterface(itf);
			binding.setTransportProtocol(bindingProtocolHttp);
			desc20.addBinding(binding);
			
			// service
			Service service = desc20.createService();
			service.setQName(new QName(desc20.getTargetNamespace(), webServiceName));
			service.setInterface(itf);
			desc20.addService(service);
			
			// endpoint [name, addressUrl, binding]
			Endpoint restEndpoint = service.createEndpoint();
			restEndpoint.setName(endpointName);
			restEndpoint.setAddress(resourceDoc.getBasePath());
			restEndpoint.setBinding(binding);
	
			service.addEndpoint(restEndpoint);
	
			for (DocumentationEndPoint api : resourceDoc.getApis()) {
				
				for (DocumentationOperation operation : api.getOperations()) {
					
					// abstract part: interface operations
					Operation wsdlAbstractOperation = WSDL20gen.getWSDLInterfaceOperation(itf, operation, modelsSchema, modelsSchema, modelsNamespace, basicTypes);
					itf.addOperation(wsdlAbstractOperation);
					
					// concrete part: binding operations
					BindingOperationImpl bindingOperation = WSDL20gen.getWSDLBindingOperation(api.getPath(), binding, wsdlAbstractOperation, operation);
					binding.addBindingOperation(bindingOperation);
	
				}
			} 				
			
			// writing wsdl
			String wsdlAsString = WSDL20gen.getWSDLDocumentAsString(modelsNamespace, desc20);
			
			return wsdlAsString;
		
		} catch (Exception e) {
			throw new BraggerException(e);
		}
		
	}
				
	/**
	 * 
	 * @param modelsNamespace
	 * @param desc20
	 * @return
	 * @throws BraggerException
	 */
	public static String getWSDLDocumentAsString(String modelsNamespace, Description desc20) {
		
		try {
			
			WSDLWriterImpl writer = new CustomWSDL20Writer();
			
			writer.useCustomNamespacesPrefixes(new String[] { 
					"wsdl" , HTTP_WWW_W3_ORG_NS_WSDL, 
					"wsdl-ext", HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS,
					"wsdl-http", HTTP_WWW_W3_ORG_NS_WSDL_HTTP,
					"wsdl-soap", HTTP_WWW_W3_ORG_NS_WSDL_SOAP ,
					"tns", desc20.getTargetNamespace() ,
					TYPES, modelsNamespace
			});
			
			Document outDoc = writer.getDocument(desc20);			
			
			//Document sortedDoc = XMLSorter.sortNodes(outDoc);
			
			// TODO validate against latest schema at http://www.w3.org/2007/06/wsdl/wsdl20.xsd
			
			return XMLPrettyPrinter.prettyPrint(outDoc, "utf8");
			
		} catch (WSDLException e) {
			throw new BraggerException(e);
		}
		
	}
	
	// ========================================================================
	
	/**
	  * 
	  * @param itf
	  * @param operation
	  * @param typesSchema
	  * @param elementsSchema
	  * @param modelsNamespace
	  * @return
	  * @throws WSDLException
	  */
	private static Operation getWSDLInterfaceOperation(InterfaceType itf, DocumentationOperation operation, Schema typesSchema, Schema elementsSchema, String modelsNamespace, Set<String> basicTypes) throws WSDLException {
		
		String targetNamespace = itf.getQName().getNamespaceURI();
		
		Operation wsdlAbstractOperation = itf.createOperation();
		wsdlAbstractOperation.setQName(new QName(targetNamespace, operation.getNickname()));
		
		// TODO this is not printed
		org.ow2.easywsdl.schema.api.Documentation opDescription = wsdlAbstractOperation.createDocumentation();
		opDescription.setContent("Summary: " + operation.getSummary() + " - Notes: " + operation.getNotes());
		wsdlAbstractOperation.setDocumentation(opDescription);
		
		try {
			// pattern is always in/out for REST services (http://www.ibm.com/developerworks/webservices/library/ws-restwsdl/)
			wsdlAbstractOperation.getOtherAttributes().put(new QName("pattern"), MEPPatternConstants.IN_OUT.value().toString());
			wsdlAbstractOperation.getOtherAttributes().put(new QName(HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS, "safe"), String.valueOf(operation.getHttpMethod().equalsIgnoreCase("get"))); // true if op is idempotent
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WSDLException(e);
		}
		
		// request message
		Input requestMessage = wsdlAbstractOperation.createInput();
		// NOTE! setting the message name on the requestMessage element, causes wrong client code generation by axis2
		Element requestElement = createMethodRequestElement(typesSchema, elementsSchema, targetNamespace, modelsNamespace, operation, basicTypes);
		requestElement.setQName(new QName(modelsNamespace, operation.getNickname() + "_request"));
		requestMessage.setElement(requestElement);
		wsdlAbstractOperation.setInput(requestMessage);
		
		// response message
		Output responseMessage = wsdlAbstractOperation.createOutput();
		// NOTE! setting the message name on the responseMessage element, causes wrong client code generation by axis2
		Element responseElement = createMethodResponseElement(typesSchema, elementsSchema, targetNamespace, modelsNamespace, operation, basicTypes);
		responseElement.setQName(new QName(modelsNamespace, operation.getNickname() + "_response"));
		responseMessage.setElement(responseElement);
		wsdlAbstractOperation.setOutput(responseMessage);
		
		// fault response messages
				
		return wsdlAbstractOperation;
	}
	
	/**
	 * 
	 * @param basePath
	 * @param binding
	 * @param wsdlAbstractOperation
	 * @param operation
	 * @return
	 */
	private static BindingOperationImpl getWSDLBindingOperation(String basePath, Binding binding, Operation wsdlAbstractOperation, DocumentationOperation operation) {
		
		String targetNamespace = binding.getQName().getNamespaceURI();
		
		BindingOperationImpl bindingOperation = (BindingOperationImpl) binding.createBindingOperation();
		bindingOperation.setQName(new QName(targetNamespace, wsdlAbstractOperation.getQName().getLocalPart())); // set the "ref" attribute

		try {
			
			bindingOperation.getOtherAttributes().put(
				new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "location"), 
				basePath);
			
			bindingOperation.getOtherAttributes().put(
				new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "method"), 
				operation.getHttpMethod());
			
			if ("POST".equals(operation.getHttpMethod()) || "PUT".equals(operation.getHttpMethod())) {
				bindingOperation.getOtherAttributes().put(
					new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "inputSerialization") , 
					"application/json");
			}
			
			bindingOperation.getOtherAttributes().put(
				new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "outputSerialization") , 
				"application/json");
			
			bindingOperation.getHttpContentEncodingDefault();
			bindingOperation.getHttpFaultSerialization();
			bindingOperation.getHttpQueryParameterSeparator();
			
		} catch (XmlException e) {
			logger.error(e.getMessage());
		}
		
		return bindingOperation;
	}
	
	/**
	 * 
	 * @param typesSchema
	 * @param elementsSchema
	 * @param targetNamespace
	 * @param modelsNamespace
	 * @param operation
	 * @return
	 */
	private static Element createMethodRequestElement(Schema typesSchema, Schema elementsSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation, Set<String> basicTypes) {

		Element requestMessageElement = elementsSchema.createElement();
		
		// set name attribute
		requestMessageElement.setQName(new QName(modelsNamespace,  WSDL20gen.TYPES + ":" + operation.getNickname() + "_request"));

		ComplexType requestType = EasyWsdlHelper.getComplexType(modelsNamespace, operation.getNickname() + "_request_type");
		requestType.setSequence(requestType.createSequence());
		
		if (operation.getParameters()!=null)
			for (DocumentationParameter docParam: operation.getParameters()) {
				
				Sequence seq = requestType.getSequence();
				
				Element paramElement = seq.createElement();
				
				System.out.println("docParam.getName():" + docParam.getName());
				System.out.println("docParam.dataType():" + docParam.dataType());
				System.out.println("docParam.getParamType():" + docParam.getParamType());
				System.out.println();
				
				// NAME
				if (docParam.getName()==null) { 
					// body param hasn't got a name
					paramElement.setQName(new QName(targetNamespace, docParam.getParamType()));
				} else { 
					// path elements and request params do have a name
					paramElement.setQName(new QName(targetNamespace, docParam.getName()));
				}
				
				// DATA TYPE
				if (basicTypes.contains(docParam.getDataType())) {
					// simple type
					paramElement.setType(mapBasicDataType(docParam.dataType(), modelsNamespace));
				} else {
					// complex type
					Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, docParam.getDataType());
					paramElement.setType(modelType);
					//paramElement.setType(EasyWsdlHelper.getSimpleType(null, "xs:string"));
				}
				
				// required
				if (docParam.getRequired()) {
					paramElement.setMinOccurs(1);
				}
				
				seq.addElement(paramElement);
				
				// add element to schema
				if (elementsSchema.getType(requestMessageElement.getQName())==null) {
					elementsSchema.addElement(requestMessageElement);
				}
				
			}
		
		// add type to schema
		if (typesSchema.getType(requestType.getQName())==null) {
			typesSchema.addType(requestType);
		}
		
		requestMessageElement.setType(requestType);
		
		return requestMessageElement;
	}

	/**
	 * 
	 * @param typesSchema
	 * @param elementsSchema
	 * @param targetNamespace
	 * @param modelsNamespace
	 * @param operation
	 * @return
	 */
	private static Element createMethodResponseElement(Schema typesSchema, Schema elementsSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation, Set<String> basicTypes) {
		
		Element responseMessageElement = elementsSchema.createElement();
		
		// set name attribute
		responseMessageElement.setQName(new QName(modelsNamespace, WSDL20gen.TYPES + ":" + operation.getNickname() + "_response"));

		// set type attribute
		String responseClass = operation.getResponseClass();
		if (basicTypes.contains(responseClass)) {
			
			Type type = mapBasicDataType(responseClass, modelsNamespace);
			
			// add type definition to the types section of the wsdl document
			if (typesSchema.getType(type.getQName())==null) {					
				typesSchema.addType(type);
			}
			
			// set this type as the type of the response
			responseMessageElement.setType(type);
			
		} else {
			
			// list of models
			if (responseClass.startsWith("List[")) {
				
				String modelName = (String) responseClass.subSequence(5, responseClass.length()-1);
				Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, modelName);
				
				// declare a new type for the list of models
				ComplexType listOfModelsType = EasyWsdlHelper.getComplexType(modelsNamespace, modelName + "List");
				Element modelListElement = typesSchema.createElement();
				modelListElement.setType(listOfModelsType);
				listOfModelsType.setSequence(listOfModelsType.createSequence());
				listOfModelsType.getSequence().addElement(listOfModelsType.getSequence().createElement());
				
				Element sequenceElement = listOfModelsType.getSequence().getElements().get(0);
				sequenceElement.setType(modelType);
				sequenceElement.setQName(new QName(modelName));
				sequenceElement.setMinOccurs(0);
				sequenceElement.setMaxOccurs("unbounded");
				sequenceElement.setNillable(true);
				
				if (typesSchema.getType(listOfModelsType.getQName())==null) {					
					typesSchema.addType(listOfModelsType);
				}
				
				responseMessageElement.setType(listOfModelsType);
				
			} else {
				
				// single model
				Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, responseClass);
				responseMessageElement.setType(modelType);
			}

		}
		

		// add element to schema
		if (elementsSchema.getType(responseMessageElement.getQName())==null) {
			elementsSchema.addElement(responseMessageElement);
		}
		
		return responseMessageElement;
	}

	/**
	 * @param dataType
	 * @param modelsNamespace
	 * @return
	 */
	private static Type mapBasicDataType(String dataType, String modelsNamespace) {
		Type type = null;
		
		if (dataType.equalsIgnoreCase("void")) {
			type = EasyWsdlHelper.getComplexType(modelsNamespace, dataType);
			ComplexType ctype = (ComplexType) type;
			ctype.setSequence(ctype.createSequence());
		}
		
		if (dataType.equalsIgnoreCase("string")) {
			type = EasyWsdlHelper.getSimpleType(null, "xs:string");
			//TODO !
//			type = EasyWsdlHelper.getSimpleType(modelsNamespace, dataType);
//			SimpleType stype = (SimpleType) type;
//			stype.setRestriction(stype.createRestriction());
//			stype.getRestriction().setBase(new QName("xs", "string"));
		}
		
		return type;
	}
	
}
