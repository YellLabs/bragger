package com.hibu.bragger.wsdl;

import java.net.URI;

import javax.xml.bind.JAXBElement;
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
import org.ow2.easywsdl.wsdl.api.BindingFault;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Fault;
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
import org.ow2.easywsdl.wsdl.impl.wsdl20.InterfaceTypeImpl;
import org.ow2.easywsdl.wsdl.impl.wsdl20.WSDLWriterImpl;
import org.ow2.easywsdl.wsdl.org.w3.ns.wsdl.InterfaceFaultType;
import org.ow2.easywsdl.wsdl.org.w3.ns.wsdl.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;
import com.hibu.bragger.swagger.SwaggerSpecs11;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
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

	/**
	 * 
	 * @param resourceName
	 * @return
	 * @throws BraggerException
	 */
	public static String generateWSDL20(String appName, String resourceName, Documentation resourceDoc, String xsdUrl) {
		
		if (resourceDoc==null) {
			throw new IllegalArgumentException("resourceName=" + resourceName + " not found");
		}

		try {
			
			String webServiceName = resourceName + WSDL20GenConstants.SERVICE_NAME_SUFFIX;
			String descriptionName = webServiceName + WSDL20GenConstants.WSDL20_DOCUMENT_NAME_SUFFIX;
			
			String interfaceName = webServiceName + WSDL20GenConstants.INTERFACE_NAME_SUFFIX; 
			String endpointName = webServiceName + WSDL20GenConstants.REST_ENDPOINT_SUFFIX;
			String restHttpBindingName = interfaceName + WSDL20GenConstants.REST_BINDING_SUFFIX;

			String targetNamespace = WSDL20GenConstants.getTargetNamespaceUri(appName, webServiceName);
			
			String bindingProtocolHttp = WSDL20GenConstants.BINDING_PROTOCOL_HTTP;
	
			// ----- global infos
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			Description desc20 = wsdlFactory.newDescription(WSDLVersionConstants.WSDL20);
			desc20.setTargetNamespace(targetNamespace);
			desc20.setQName(new QName(desc20.getTargetNamespace(), descriptionName));
	
			
			// ----- types
			Types types = desc20.createTypes();
			
			// first schema is only to include the types generated via JAXB from models
			Schema modelsSchema = types.createSchema();
			modelsSchema.setTargetNamespace(WSDL20GenConstants.getModelsNamespaceUri(appName));
			Include includeDef = modelsSchema.createInclude();
			includeDef.setLocationURI(new URI(resourceDoc.getBasePath() + xsdUrl));
			modelsSchema.addInclude(includeDef);
			types.addSchema(modelsSchema);
			desc20.setTypes(types);
			
			// generic error type
			createGenericError(modelsSchema, appName);
			
			
			// ----- interface
			InterfaceType itf = desc20.createInterface();
			itf.setQName(new QName(desc20.getTargetNamespace(), interfaceName));
			
			desc20.addInterface(itf);
						
			// ----- binding
			Binding binding = desc20.createBinding();
			binding.setQName(new QName(desc20.getTargetNamespace(), restHttpBindingName));
			binding.setInterface(itf);
			binding.setTransportProtocol(bindingProtocolHttp);
			desc20.addBinding(binding);
			
			// ----- service
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
	
			if (resourceDoc.getApis()!=null) {
			for (DocumentationEndPoint api : resourceDoc.getApis()) {
					
					if (api.getOperations()!=null) {
					for (DocumentationOperation operation : api.getOperations()) {
							
							// abstract part: interface operations
							Operation wsdlAbstractOperation = getWSDLInterfaceOperation(itf, service, operation, modelsSchema, appName);
							itf.addOperation(wsdlAbstractOperation);
							
							// concrete part: binding operations
							BindingOperationImpl bindingOperation = getWSDLBindingOperation(api.getPath(), service, binding, wsdlAbstractOperation, operation);
														
							binding.addBindingOperation(bindingOperation);
					}}
			}}
			
			// ----- writing wsdl
			String wsdlAsString = getWSDLDocumentAsString(appName, desc20);
			
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
	public static String getWSDLDocumentAsString(String appName, Description desc20) {
		
		try {
			
			WSDLWriterImpl writer = new CustomWSDL20Writer();
			
			writer.useCustomNamespacesPrefixes(new String[] { 
					"wsdl" , WSDL20GenConstants.HTTP_WWW_W3_ORG_NS_WSDL, 
					"wsdl-ext", WSDL20GenConstants.HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS,
					"wsdl-http", WSDL20GenConstants.HTTP_WWW_W3_ORG_NS_WSDL_HTTP,
					"wsdl-soap", WSDL20GenConstants.HTTP_WWW_W3_ORG_NS_WSDL_SOAP ,
					"tns", desc20.getTargetNamespace() ,
					WSDL20GenConstants.getModelsNamespacePrefix(), WSDL20GenConstants.getModelsNamespaceUri(appName)
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
	private static Operation getWSDLInterfaceOperation(InterfaceType itf, Service service, DocumentationOperation operation, Schema typesSchema, String appName) throws WSDLException {
		
		String targetNamespace = itf.getQName().getNamespaceURI();
		String modelsNamespace = WSDL20GenConstants.getModelsNamespaceUri(appName);
		
		Operation wsdlAbstractOperation = itf.createOperation();
		wsdlAbstractOperation.setQName(new QName(targetNamespace, operation.getNickname()));
		
		// FIXME this is not printed
		org.ow2.easywsdl.schema.api.Documentation opDescription = wsdlAbstractOperation.createDocumentation();
		opDescription.setContent("Summary: " + operation.getSummary() + " - Notes: " + operation.getNotes());
		wsdlAbstractOperation.setDocumentation(opDescription);
		
		try {
			// pattern is always in/out for REST services (http://www.ibm.com/developerworks/webservices/library/ws-restwsdl/)
			wsdlAbstractOperation.getOtherAttributes().put(new QName("pattern"), MEPPatternConstants.IN_OUT.value().toString());
			wsdlAbstractOperation.getOtherAttributes().put(new QName(WSDL20GenConstants.HTTP_WWW_W3_ORG_NS_WSDL_EXTENSIONS, "safe"), String.valueOf(operation.getHttpMethod().equalsIgnoreCase("get"))); // true if op is idempotent
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WSDLException(e);
		}
		
		
		// ----- request message
		// if the operation in the swagger specs has no parameters 
		// then no input message request message should be added to the wsdl
		Input requestMessage = wsdlAbstractOperation.createInput();
		if (operation.getParameters()!=null && !operation.getParameters().isEmpty()) {
			Element requestElement = createMethodRequestElement(typesSchema, targetNamespace, modelsNamespace, operation);
			requestElement.setQName(new QName(modelsNamespace,  operation.getNickname() + "_request"));
			requestMessage.setElement(requestElement);
		} else { 
			// NOTE! setting the message name on the requestMessage element, causes the axis wsdl2java tool to ignore the element
			//requestMessage.setMessageName(new QName(modelsNamespace, operation.getNickname() + "_request"));
			Element requestMessageElement = typesSchema.createElement();
			requestMessageElement.setRef(new QName("#none"));
			requestMessage.setElement(requestMessageElement);
		}
		wsdlAbstractOperation.setInput(requestMessage);
		
		
		// ----- response message
		// even if the operation in the swagger specs has no response class
		// an EmptyResponse message is defined anyway to workaround a bug in the wsdl2java generation tool
		// can't use a #none ref here like in the request element as the axis jaxbri databinding used in wsdl2java wouldn't be able to generate the client code
		Output responseMessage = wsdlAbstractOperation.createOutput();
		if (!SwaggerSpecs11.isVoidType(operation.getResponseClass())) {			
			Element responseElement = createMethodResponseElement(typesSchema, targetNamespace, modelsNamespace, operation);
			responseElement.setQName(new QName(modelsNamespace, operation.getNickname() + "_response"));
			responseMessage.setElement(responseElement);
		} else {
			Element responseMessageElement = typesSchema.createElement();
			responseMessageElement.setRef(new QName("#none"));
			responseMessage.setElement(responseMessageElement);
		}
		wsdlAbstractOperation.setOutput(responseMessage);
		
	
		// ----- fault messages
		InterfaceTypeImpl itfImpl = (InterfaceTypeImpl) itf;
		InterfaceFaultType itfFault = new ObjectFactory().createInterfaceFaultType();
		itfFault.setName(service.getQName().getLocalPart() + "Error");
		itfFault.setElement(WSDL20GenConstants.getModelsNamespacePrefix() + ":" + WSDL20GenConstants.ERROR_RESPONSE_ELEMENTNAME);
		
		JAXBElement<InterfaceFaultType> jaxbFault = new JAXBElement<InterfaceFaultType>(new QName("http://www.w3.org/ns/wsdl", "fault"), InterfaceFaultType.class, InterfaceType.class, itfFault);
		itfImpl.getModel().getOperationOrFaultOrAny().add(jaxbFault);
		
		Fault outFault = wsdlAbstractOperation.createFault();
		outFault.setElement(EasyWsdlHelper.getElement(targetNamespace, service.getQName().getLocalPart() + "Error"));
		wsdlAbstractOperation.addFault(outFault);
		
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
	private static BindingOperationImpl getWSDLBindingOperation(String basePath, Service service, Binding binding, Operation wsdlAbstractOperation, DocumentationOperation operation) {
		
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
			
			// always bind the generic status codes plus any other errors defined in the api through the annotation @ApiErrors
			addBindingFault(service, targetNamespace, bindingOperation, 500);
			addBindingFault(service, targetNamespace, bindingOperation, 404);
			addBindingFault(service, targetNamespace, bindingOperation, 400);
			if (operation.getErrorResponses()!=null) {
				for (DocumentationError docError : operation.getErrorResponses()) {
					if (docError!=null && docError.getCode()!=500 && docError.getCode()!=404 && docError.getCode()!=400)
						addBindingFault(service, targetNamespace, bindingOperation, docError.getCode());
				}
			}
			
			
		} catch (XmlException e) {
			logger.error(e.getMessage());
		}
		
		return bindingOperation;
	}
	
	/**
	 * returns an element for the operation request message
	 * e.g. <xs:element name="operationName_request" type="models:operationName_request_type"/>
	 * 
	 * @param typesSchema
	 * @param elementsSchema
	 * @param targetNamespace
	 * @param modelsNamespace
	 * @param operation
	 * @return
	 */
	private static Element createMethodRequestElement(Schema typesSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation) {

		Element requestMessageElement = typesSchema.createElement();
		
		// set name attribute
		// FIXME the namespace prefix should be added from the parameter modelNamespace
		requestMessageElement.setQName(new QName(modelsNamespace, WSDL20GenConstants.getModelsNamespacePrefix() + ":" + operation.getNickname() + "_request"));

		ComplexType requestMessageType = EasyWsdlHelper.getComplexType(modelsNamespace, operation.getNickname() + "_request_type");
		requestMessageType.setSequence(requestMessageType.createSequence());
		
		if (operation.getParameters()!=null) {

			Sequence seq = requestMessageType.getSequence();
			
			for (DocumentationParameter docParam: operation.getParameters()) {
				
				Element paramElement = seq.createElement();
				
				System.out.println("docParam.getName():" + docParam.getName());
				System.out.println("docParam.dataType():" + docParam.dataType());
				System.out.println("docParam.getParamType():" + docParam.getParamType());
				System.out.println();

				// NAME
				if ("body".equals(docParam.getParamType())) { 
					paramElement.setQName(new QName(targetNamespace, "body"));
				} else { 
					// path elements and request params
					paramElement.setQName(new QName(targetNamespace, docParam.getName()));
				}
				
				// DATA TYPE
				String dataType = docParam.dataType();
				if (SwaggerSpecs11.isVoidType(dataType)) {
					
				} else if (SwaggerSpecs11.isPrimitiveType(dataType)) {
					
					paramElement.setType(SwaggerSpecs11.mapBasicDataType(dataType, modelsNamespace, typesSchema));
					
				} else {
					
					if (SwaggerSpecs11.isContainerType(dataType)) {

						String modelName = SwaggerSpecs11.getModelSimpleName(dataType);
												
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
							System.out.println("adding type " + listOfModelsType.getQName() + " to schema");
							typesSchema.addType(listOfModelsType);
						}
						
						paramElement.setType(listOfModelsType);
						
					} else {
					
						// complex type
						Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, dataType);
						paramElement.setType(modelType);
					}
				}
				
				// required
				if (docParam.getRequired()) {
					paramElement.setMinOccurs(1);
				}
				
				seq.addElement(paramElement);			
			}

		}
		
		requestMessageElement.setType(requestMessageType);

		// add element to schema
		if (typesSchema.getElement(requestMessageElement.getQName())==null) {
			System.out.println("adding element " + requestMessageElement.getQName() + " to schema");
			typesSchema.addElement(requestMessageElement);
		}
		
		// add request message type to schema
		if (typesSchema.getType(requestMessageType.getQName())==null) {
			System.out.println("adding type " + requestMessageType.getQName() + " to schema");
			typesSchema.addType(requestMessageType);
		}
		
		
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
	private static Element createMethodResponseElement(Schema typesSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation) {
		
		Element responseMessageElement = typesSchema.createElement();

		// set name attribute
		responseMessageElement.setQName(new QName(modelsNamespace, WSDL20GenConstants.getModelsNamespacePrefix() + ":" + operation.getNickname() + "_response"));

		// set type attribute
		String responseClass = operation.getResponseClass();
		if (SwaggerSpecs11.isVoidType(responseClass)) {

			Type type = SwaggerSpecs11.mapVoidDataType(WSDL20GenConstants.EMPTY_RESPONSE_TYPENAME, modelsNamespace, typesSchema);
			responseMessageElement.setType(type);
			
		} else if (SwaggerSpecs11.isPrimitiveType(responseClass)) {
			
			Type type = SwaggerSpecs11.mapBasicDataType(responseClass, modelsNamespace, typesSchema);
			responseMessageElement.setType(type);
			
		} else {
			
			// list of models
			if (SwaggerSpecs11.isContainerType(responseClass)) {

				String modelName = SwaggerSpecs11.getModelSimpleName(responseClass);
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
					System.out.println("adding type " + listOfModelsType.getQName() + " to schema");
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
		if (typesSchema.getElement(responseMessageElement.getQName())==null) {
			System.out.println("adding element " + responseMessageElement.getQName() + " to schema");
			typesSchema.addElement(responseMessageElement);
		}
		
		return responseMessageElement;
	}
	
	private static void createGenericError(Schema typesSchema, String appName) {
		
		ComplexType internalServerErrorType = EasyWsdlHelper.getComplexType(WSDL20GenConstants.getModelsNamespaceUri(appName), WSDL20GenConstants.ERROR_RESPONSE_TYPENAME);
		Sequence sequence = internalServerErrorType.createSequence();
		internalServerErrorType.setSequence(sequence);
		
		Element errorCodeElement = sequence.createElement();
		errorCodeElement.setQName(new QName("code"));
		errorCodeElement.setType(EasyWsdlHelper.getSimpleType(null, "xs:int"));
		errorCodeElement.setMinOccurs(0);
		errorCodeElement.setMaxOccurs("1");
		sequence.addElement(errorCodeElement);
		
		Element errorTypeElement = sequence.createElement();
		errorTypeElement.setQName(new QName("type"));
		errorTypeElement.setType(EasyWsdlHelper.getSimpleType(null, "xs:string"));
		errorTypeElement.setMinOccurs(0);
		errorTypeElement.setMaxOccurs("1");
		sequence.addElement(errorTypeElement);
		
		Element errorMessageElement = sequence.createElement();
		errorMessageElement.setQName(new QName("message"));
		errorMessageElement.setType(EasyWsdlHelper.getSimpleType(null, "xs:string"));
		errorMessageElement.setMinOccurs(0);
		errorMessageElement.setMaxOccurs("1");
		sequence.addElement(errorMessageElement);
		
		Element internalServerErrorElement = typesSchema.createElement();
		internalServerErrorElement.setType(internalServerErrorType);
		internalServerErrorElement.setQName(new QName(WSDL20GenConstants.ERROR_RESPONSE_ELEMENTNAME));
		
		// add element to schema
		if (typesSchema.getElement(internalServerErrorElement.getQName())==null) {
			System.out.println("adding element " + internalServerErrorElement.getQName() + " to schema");
			typesSchema.addElement(internalServerErrorElement);
		}
		
		// add type to schema
		if (typesSchema.getType(internalServerErrorType.getQName())==null) {
			System.out.println("adding type " + internalServerErrorType.getQName() + " to schema");
			typesSchema.addType(internalServerErrorType);
		}

	}
	
	private static void addBindingFault(Service service, String targetNamespace, BindingOperationImpl bindingOperation, Integer statusCode) throws XmlException {
		BindingFault bindingFault = bindingOperation.createFault();
		bindingFault.setRef(new QName(targetNamespace, service.getQName().getLocalPart() + "Error"));
		bindingFault.getOtherAttributes().put(new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "code"), statusCode.toString());
		bindingOperation.addFault(bindingFault);
	}

}
