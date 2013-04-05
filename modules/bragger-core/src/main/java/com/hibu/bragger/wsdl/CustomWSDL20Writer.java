package com.hibu.bragger.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.impl.wsdl20.Constants;
import org.ow2.easywsdl.wsdl.impl.wsdl20.WSDLJAXBContext;
import org.ow2.easywsdl.wsdl.impl.wsdl20.WSDLWriterImpl;
import org.ow2.easywsdl.wsdl.org.w3.ns.wsdl.DescriptionType;
import org.w3c.dom.Document;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class CustomWSDL20Writer extends WSDLWriterImpl {

	private String[] customPrefixes = null;

	private DocumentBuilderFactory builder = null;
	
	public CustomWSDL20Writer() throws WSDLException {
		WSDLJAXBContext.getInstance();

		builder = DocumentBuilderFactory.newInstance();
		builder.setNamespaceAware(true);
	}

	/**
	 * Build the XML nodes from the WSDL descriptor in Java classes form.
	 * @param schemaLocation 
	 * 
	 * @param EndpointReferenceDescriptorClass
	 *            The EndpointReference Descriptor root class
	 * @param EndpointReferenceDescriptorNode
	 *            The XML Node to fill with the EndpointReference descriptor XML nodes
	 */
	@SuppressWarnings("unchecked")
	public Document convertWSDL20Description2DOMElement(final DescriptionType wsdlDescriptor, String schemaLocation) throws WSDLException {
		
		Document doc = null;
		
		try {
			
			doc = builder.newDocumentBuilder().newDocument();

			@SuppressWarnings("rawtypes")
			final JAXBElement element = 
				new JAXBElement(new QName(Constants.WSDL_20_NAMESPACE, Constants.WSDL20_ROOT_TAG), wsdlDescriptor.getClass(), wsdlDescriptor);

			Marshaller marshaller = WSDLJAXBContext.getInstance().getJaxbContext().createMarshaller();
			NamespacePrefixMapper mapper = null;
			if (this.customPrefixes != null) {
				mapper = new CustomPrefixMapper(customPrefixes);
			} else {
				mapper = new CustomPrefixMapper();
			}
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
			if (schemaLocation != null) {
				marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
			}
			marshaller.marshal(element, doc);

		} catch (final JAXBException ex) {
			throw new WSDLException("Failed to build XML binding from WSDL descriptor Java classes", ex);
			
		} catch (final ParserConfigurationException ex) {
			throw new WSDLException("Failed to build XML binding from WSDL descriptor Java classes", ex);

		}
		
		return doc;
	}
	
	/**
	 * Method used to set predefined namespace prefixes.
	 */
	public void useCustomNamespacesPrefixes(String[] customPrefixes) throws WSDLException {
		this.customPrefixes = customPrefixes.clone();
	}

	/**
	 * Method used to set normalized namespace prefixes.
	 */
	public void useNormalizedNamespacesPrefixes() throws WSDLException {
		this.customPrefixes = null;
	}

}
