package com.hibu.bragger.codegen.axis2;

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
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.json.JSONDataSource;
import org.apache.axis2.json.JSONMessageFormatter;
import org.apache.axis2.transport.http.util.URIEncoderDecoder;
import org.apache.axis2.transport.http.util.URLTemplatingUtil;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class NSIgnoreJSONFormatter extends JSONMessageFormatter {
	
	private Set<String> arraysOfPrimitives;
	
	public NSIgnoreJSONFormatter() {
		super();
		this.arraysOfPrimitives = new HashSet<String>();
	}
	
	public NSIgnoreJSONFormatter(Set<String> arraysOfPrimitives) {
		super();
		this.arraysOfPrimitives = arraysOfPrimitives;
	}
	
	// ========================================================================
	
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

    @Override
    protected String getStringToWrite(OMDataSource dataSource) {
        if (dataSource instanceof JSONDataSource) {
            return ((JSONDataSource)dataSource).getCompleteJOSNString();
        } else {
            return null;
        }
    }
    
}
