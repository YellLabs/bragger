package com.hibu.bragger.utils;

import java.io.StringBufferInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.woden.ErrorHandler;
import org.apache.woden.ErrorInfo;
import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.WSDLSource;
import org.apache.woden.internal.DOMWSDLSource;
import org.xml.sax.InputSource;

public class Wsdl20Validator implements ErrorHandler {

    private boolean valid;

    public Wsdl20Validator(URL wsdlurl) {

		valid = true;

		try {
			
		    WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		    reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
		    reader.getErrorReporter().setErrorHandler(this);
		    
		    reader.readWSDL(wsdlurl.toString());
		    
        } catch (WSDLException e) {
		    System.err.println("Something went really wrong!");
		    System.err.println(e);
		    System.exit(1);
		    return;
        }
    }

    public Wsdl20Validator(String wsdlDocumentAsString) {
    	
    	try {
    		
	        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		    reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
		    reader.getErrorReporter().setErrorHandler(this);
		    
		    WSDLSource wsdlSource = new DOMWSDLSource(reader.getErrorReporter());
		    wsdlSource.setSource(new InputSource(new StringBufferInputStream(wsdlDocumentAsString)));
		    wsdlSource.setBaseURI(new URI("http://localhost/wsdl"));
		    
		    reader.readWSDL(wsdlSource);
		    
        } catch (WSDLException e) {
		    System.err.println("Something went really wrong!");
		    System.err.println(e);
		    return;
        } catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
    public void warning(ErrorInfo e) {
        logError("Warning", e);
    }
    
    public void error(ErrorInfo e) {
        logError("Error", e);
        valid = false;
    }
    
    public void fatalError(ErrorInfo e) {
        logError("Fatal", e);
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    private void logError(String type, ErrorInfo e) {
    	System.out.println("[" + type + "]\t" + e);
    }
    
}
