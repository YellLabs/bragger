package com.hibu.bragger.codegen.axis2;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.json.AbstractJSONDataSource;
import org.codehaus.jettison.json.JSONTokener;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;

public class WrappingObjectJSONDatasource extends AbstractJSONDataSource {

	private boolean jsonResponseWrappedInAContainerObject;
	private Map<String, String> XMLToJSNNamespaceMap;
	
	public WrappingObjectJSONDatasource(Reader jsonReader, String localName, boolean wrappedResponse) {
		super(jsonReader, localName);
		this.jsonResponseWrappedInAContainerObject = wrappedResponse;
	}

	public WrappingObjectJSONDatasource(Reader jsonReader, String localName, boolean wrappedResponse, Map<String, String> XMLToJSNNamespaceMap) {
		this(jsonReader, localName, wrappedResponse);
		this.jsonResponseWrappedInAContainerObject = wrappedResponse;
		this.XMLToJSNNamespaceMap = XMLToJSNNamespaceMap;
	}
	
    @Override
    public XMLStreamReader getReader() throws XMLStreamException {

    	if (this.XMLToJSNNamespaceMap==null) {
    		this.XMLToJSNNamespaceMap = new HashMap<String, String>();
    		this.XMLToJSNNamespaceMap.put("", "");
    	}    	

        //input factory for "Mapped" convention
        MappedXMLInputFactory inputFactory = new MappedXMLInputFactory(this.XMLToJSNNamespaceMap);
        
        String jsonString = "";
        
        if (jsonResponseWrappedInAContainerObject) {        	
        	jsonString = "{" + localName + ":" + this.getJSONString();
        } else {
        	jsonString = "{" + localName + ":" + this.getJSONString() + "}";
        }
        
        return inputFactory.createXMLStreamReader(new JSONTokener(jsonString));
    }

}
