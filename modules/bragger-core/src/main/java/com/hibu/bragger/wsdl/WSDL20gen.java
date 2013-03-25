package com.hibu.bragger.wsdl;

import java.net.URI;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Documentation;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Import;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.tooling.java2wsdl.util.XMLSorter;
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
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

/**
 *  this class generates a WSDL20 document form API metadata information
 *  provided by swagger
 * @author paolo
 *
 */
public class WSDL20gen {
	
	/**
	 * 
	 * @param resourceName
	 * @return
	 * @throws BraggerException
	 */
	public static String generateWSDL20(String resourceName, com.wordnik.swagger.core.Documentation resourceDoc, Set<String> basicTypes) {
		
		if (resourceDoc==null) {
			throw new IllegalArgumentException("resourceName=" + resourceName + " not found");
		}

		try {

		
			String webServiceName = resourceName + "_service";
			String descriptionName = webServiceName + "_wsdl20_document";
			String mainServiceNamespace = "http://www.hibu.com/ns/" + webServiceName;
			String targetNamespace = mainServiceNamespace + "/wsdl";
			String modelsNamespace = mainServiceNamespace + "/xsd";  // maybe add the package name so to avoid clashed e.g. placesapi Place and bps Place
			String interfaceName = webServiceName + "_interface"; 
			String endpointName = webServiceName + "_rest_endpoint";
			String restHttpBindingName = interfaceName + "_rest_binding";
			String bindingProtocolHttp = "http";
	
			// global infos
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			Description desc20 = wsdlFactory.newDescription(WSDLVersionConstants.WSDL20);
			desc20.setTargetNamespace(targetNamespace);
			desc20.setQName(new QName(desc20.getTargetNamespace(), descriptionName));
	
			// types
			Types types = desc20.createTypes();
			
			// first schema is only to import the types generated via JAXB from models
			Schema modelsSchema = types.createSchema();
			Import importDef = modelsSchema.createImport();
			importDef.setLocationURI(new URI(resourceDoc.getBasePath() + "/api-docs.xsd")); // TODO use reverse route only if not found use a default
			importDef.setNamespaceURI(modelsNamespace);
			modelsSchema.addImport(importDef);
			types.addSchema(modelsSchema);
	
			// second schema for utility types created by-needs 
			// e.g. Model is part of the first schema, List[Model] goes here
			// e.g. definition of types like "void"
			Schema messageTypesSchema = types.createSchema();
			types.addSchema(messageTypesSchema);
	
			// third schema contains only the elements referenced from the service interface operations
			Schema messageElementsSchema = types.createSchema();
			types.addSchema(messageElementsSchema);
			
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
					Operation wsdlAbstractOperation = WSDL20gen.getWSDLInterfaceOperation(itf, operation, messageTypesSchema, messageElementsSchema, modelsNamespace, basicTypes);
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
					"wsdl20" , "http://www.w3.org/ns/wsdl", 
					"wsdl20-ext", "http://www.w3.org/ns/wsdl-extensions",
					"wsdl20-http", "http://www.w3.org/ns/wsdl/http",
					"wsdl20-soap", "http://www.w3.org/ns/wsdl/soap" ,
					"tns", desc20.getTargetNamespace() ,
					"models", modelsNamespace
			});
			
			Document outDoc = writer.getDocument(desc20);			
			
			Document sortedDoc = XMLSorter.sortNodes(outDoc);
			
			// TODO validate against latest schema at http://www.w3.org/2007/06/wsdl/wsdl20.xsd
			
			return XMLPrettyPrinter.prettyPrint(sortedDoc, "utf8");
			
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
		Documentation opDescription = wsdlAbstractOperation.createDocumentation();
		opDescription.setContent("Summary: " + operation.getSummary() + " - Notes: " + operation.getNotes());
		wsdlAbstractOperation.setDocumentation(opDescription);
		
		try {
			// pattern is always in/out for REST services (http://www.ibm.com/developerworks/webservices/library/ws-restwsdl/)
			wsdlAbstractOperation.getOtherAttributes().put(new QName("pattern"), MEPPatternConstants.IN_OUT.value().toString());
			wsdlAbstractOperation.getOtherAttributes().put(new QName("http://www.w3.org/ns/wsdl-extensions", "safe"), String.valueOf(operation.getHttpMethod().equalsIgnoreCase("get"))); // true if op is idempotent
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WSDLException(e);
		}
		
		// request message
		Input requestMessage = wsdlAbstractOperation.createInput();
		requestMessage.setMessageName(new QName(targetNamespace, operation.getNickname() + "_in"));
		requestMessage.setElement(createMethodRequestElement(typesSchema, elementsSchema, targetNamespace, modelsNamespace, operation));
		wsdlAbstractOperation.setInput(requestMessage);
		
		// response message
		Output responseMessage = wsdlAbstractOperation.createOutput();
		responseMessage.setMessageName(new QName(targetNamespace, operation.getNickname() + "_out"));
		responseMessage.setElement(createMethodResponseElement(typesSchema, elementsSchema, targetNamespace, modelsNamespace, operation, basicTypes));
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
			
			bindingOperation.getOtherAttributes().put(new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "location"), basePath);
			bindingOperation.getOtherAttributes().put(new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "method"), operation.getHttpMethod());
			bindingOperation.getOtherAttributes().put(new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "inputSerialization") , "application/json");
			bindingOperation.getOtherAttributes().put(new QName(BindingConstants.HTTP_BINDING4WSDL20.value().toString(), "outputSerialization") , "application/json");
			bindingOperation.getHttpContentEncodingDefault();
			bindingOperation.getHttpFaultSerialization();
			bindingOperation.getHttpQueryParameterSeparator();
			
		} catch (XmlException e) {
			// TODO use plain logback to removedependency from Play
			// Logger.error(e.getMessage());
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
	private static Element createMethodResponseElement(Schema typesSchema, Schema elementsSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation, Set<String> basicTypes) {
		
		Element responseMessageElement = elementsSchema.createElement();
		
		// set name attribute
		responseMessageElement.setQName(new QName(targetNamespace, operation.getNickname() + "_response"));

		// set type attribute
		if (basicTypes.contains(operation.getResponseClass())) {
			
			String respType = operation.getResponseClass();
			Type type = null;
			
			if (respType.equalsIgnoreCase("void")) {
				type = EasyWsdlHelper.getComplexType(targetNamespace, respType);
				ComplexType ctype = (ComplexType) type;
				ctype.setSequence(ctype.createSequence());
			}
			
			if (respType.equalsIgnoreCase("string")) {
				type = EasyWsdlHelper.getSimpleType(targetNamespace, respType);
				SimpleType stype = (SimpleType) type;
				stype.setRestriction(stype.createRestriction());
				stype.getRestriction().setBase(new QName("xs", "string"));
			}
			
			if (typesSchema.getType(type.getQName())==null) {					
				typesSchema.addType(type);
			}
			
			responseMessageElement.setType(type);
			
		} else {
			
			// list of models
			if (operation.getResponseClass().startsWith("List[")) {
				
				String modelName = (String) operation.getResponseClass().subSequence(5, operation.getResponseClass().length()-1);
				Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, modelName);
				
				// declare a new type for the list of models
				ComplexType listOfModelsType = EasyWsdlHelper.getComplexType(targetNamespace, operation.getResponseClass());
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
				Type modelType = EasyWsdlHelper.getComplexType(modelsNamespace, operation.getResponseClass());
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
	 * 
	 * @param typesSchema
	 * @param elementsSchema
	 * @param targetNamespace
	 * @param modelsNamespace
	 * @param operation
	 * @return
	 */
	private static Element createMethodRequestElement(Schema typesSchema, Schema elementsSchema, String targetNamespace, String modelsNamespace, DocumentationOperation operation) {

		Element requestMessageElement = elementsSchema.createElement();
		
		// set name attribute
		requestMessageElement.setQName(new QName(targetNamespace, operation.getNickname() + "_request"));

		ComplexType requestType = EasyWsdlHelper.getComplexType(targetNamespace, operation.getNickname() + "_request_type");
		requestType.setSequence(requestType.createSequence());
		
		if (operation.getParameters()!=null)
			for (DocumentationParameter docParam: operation.getParameters()) {
				
				Sequence seq = requestType.getSequence();
				
				Element paramElement = seq.createElement();
				
				// param name
				if (docParam.getName()==null) {
					paramElement.setQName(new QName(targetNamespace, docParam.getParamType()));
				} else {
					paramElement.setQName(new QName(targetNamespace, docParam.getName()));
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
		
		return requestMessageElement;
	}

}
