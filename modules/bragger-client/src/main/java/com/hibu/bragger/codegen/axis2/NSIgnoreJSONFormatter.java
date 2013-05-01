package com.hibu.bragger.codegen.axis2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.json.AbstractJSONMessageFormatter;
import org.apache.axis2.json.JSONDataSource;
import org.apache.axis2.transport.http.util.URIEncoderDecoder;
import org.apache.axis2.transport.http.util.URLTemplatingUtil;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class NSIgnoreJSONFormatter extends AbstractJSONMessageFormatter {
	
	private Set<String> arraysOfPrimitives;
	private boolean useBodyContainer;
	
	/**
	 * 
	 */
	public NSIgnoreJSONFormatter() {
		super();
		this.arraysOfPrimitives = new HashSet<String>();
		this.useBodyContainer = false;
	}
	
	/**
	 * 
	 * @param arraysOfPrimitives
	 */
	public NSIgnoreJSONFormatter(Set<String> arraysOfPrimitives) {
		this();
		if (arraysOfPrimitives!=null) {			
			this.arraysOfPrimitives = arraysOfPrimitives;
		}
	}

	/**
	 * 
	 * @param arraysOfPrimitives
	 * @param useBodyContainer
	 */
	public NSIgnoreJSONFormatter(Set<String> arraysOfPrimitives, boolean useBodyContainer) {
		this(arraysOfPrimitives);
		this.useBodyContainer = useBodyContainer;
	}
	
	// ========================================================================
    
    @Override
    public URL getTargetAddress(MessageContext msgCtxt, OMOutputFormat format, URL targetURL) throws AxisFault {

        String httpMethod = (String) msgCtxt.getProperty(Constants.Configuration.HTTP_METHOD);
        
        OMElement dataOut = msgCtxt.getEnvelope().getBody().getFirstElement();

        //if the http method is GET, send the json string as a parameter
        if (dataOut != null && (httpMethod != null) && Constants.Configuration.HTTP_METHOD_GET.equalsIgnoreCase(httpMethod)) {
            try {
                String jsonString;
                if (dataOut instanceof OMSourcedElement && getStringToWrite(((OMSourcedElement) dataOut).getDataSource()) != null) {
                    jsonString = getStringToWrite(((OMSourcedElement) dataOut).getDataSource());
                } else {
                    StringWriter out = new StringWriter();
                    XMLStreamWriter jsonWriter = getJSONWriter(out);
                    dataOut.serializeAndConsume(jsonWriter);
                    jsonWriter.writeEndDocument();
                    jsonString = out.toString();
                }
                jsonString = URIEncoderDecoder.quoteIllegal(jsonString, WSDL2Constants.LEGAL_CHARACTERS_IN_URL);
                String param = "query=" + jsonString;
                String returnURLFile = targetURL.getFile() + "?" + param;

                return new URL(targetURL.getProtocol(), targetURL.getHost(), targetURL.getPort(), returnURLFile);
                
            } catch (MalformedURLException e) {
                throw AxisFault.makeFault(e);
                
            } catch (XMLStreamException e) {
                throw AxisFault.makeFault(e);
                
            } catch (UnsupportedEncodingException e) {
                throw AxisFault.makeFault(e);
            }
            
        } else {
        	
        	// see org.apache.axis2.transport.http.XFormURLEncodedFormatter.getTargetAddress
        	// TODO finish implementation of this branch, try to consider all scenarios PUT/DELETE....
            targetURL = URLTemplatingUtil.getTemplatedURL(targetURL, msgCtxt, true);
            return targetURL;
        }
    }

	@Override public void writeTo(MessageContext msgCtxt, OMOutputFormat format, OutputStream out, boolean preserve) throws AxisFault {
		
		OMElement element = msgCtxt.getEnvelope().getBody().getFirstElement();
		
		try {
			
			// Mapped format cannot handle element with namespaces.. So cannot handle Faults
			if (element instanceof SOAPFault) {
				SOAPFault fault = (SOAPFault) element;
				OMElement element2 = element.getOMFactory().createOMElement("Fault", null);
				element2.setText(fault.toString());
				element = element2;
			}
			
			if (element instanceof OMSourcedElement && getStringToWrite(((OMSourcedElement) element).getDataSource()) != null) {
				String jsonToWrite = getStringToWrite(((OMSourcedElement) element).getDataSource());
				out.write(jsonToWrite.getBytes());
				
			} else {
				XMLStreamWriter jsonWriter = getJSONWriter(out, format);
				
				// TODO: implement this also for the other if branch (when there's a OMDatasource)
				// here i've changed the implementation found in AbstractJSONFormatter
				// to remove the container object body. e.g. { "body" : { <model> }
				if (useBodyContainer) {					
					element.serializeAndConsume(jsonWriter);
				} else {					
					OMElement body = element.getFirstElement();
					body.serializeAndConsume(jsonWriter);
				}
				
				jsonWriter.writeEndDocument();
			}
			
		} catch (IOException e) {
			throw AxisFault.makeFault(e);
			
		} catch (XMLStreamException e) {
			throw AxisFault.makeFault(e);
			
		} catch (IllegalStateException e) {
			throw new AxisFault(
					"Mapped formatted JSON with namespaces are not supported in Axis2. "
							+ "Make sure that your request doesn't include namespaces or "
							+ "use the Badgerfish convention");
		}
	}
 
    @Override
    protected String getStringToWrite(OMDataSource dataSource) {
        if (dataSource instanceof JSONDataSource) {
            return ((JSONDataSource)dataSource).getCompleteJOSNString();
        } else {
            return null;
        }
    }

    @Override
    protected XMLStreamWriter getJSONWriter(Writer writer) {
    	
    	Configuration c = new Configuration();
    	c.setIgnoreNamespaces(true);
        MappedNamespaceConvention mnc = new MappedNamespaceConvention(c);
        
        MappedXMLStreamWriter mappedXMLStreamWriter = new MappedXMLStreamWriter(mnc, writer);
        
        // workaround for well known jettison bug
        // http://jira.codehaus.org/browse/JETTISON-22
        // http://jira.codehaus.org/browse/JETTISON-102
        // the workaround sucks, hopefully they will fix it ....
        for (String arrayName : arraysOfPrimitives) {			
        	mappedXMLStreamWriter.serializeAsArray(arrayName);
		}
        
		return mappedXMLStreamWriter;
    }
    
    private XMLStreamWriter getJSONWriter(OutputStream outStream, OMOutputFormat format) throws AxisFault {
        try {
            return getJSONWriter(new OutputStreamWriter(outStream, format.getCharSetEncoding()));
        } catch (UnsupportedEncodingException ex) {
            throw AxisFault.makeFault(ex);
        }
    }
    
}
