package com.hibu.bragger.codegen.axis2;

import java.io.Reader;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.json.AbstractJSONDataSource;
import org.codehaus.jettison.json.JSONTokener;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;

public class WrappingObjectJSONDatasource extends AbstractJSONDataSource {

	private boolean jsonResponseWrappedInAContainerObject;
	
	public WrappingObjectJSONDatasource(Reader jsonReader, String localName, boolean wrappedResponse) {
		super(jsonReader, localName);
		this.jsonResponseWrappedInAContainerObject = wrappedResponse;
	}

    @Override
    public XMLStreamReader getReader() throws XMLStreamException {

        HashMap XMLToJSNNamespaceMap = new HashMap();
        XMLToJSNNamespaceMap.put("", "");

        //input factory for "Mapped" convention
        MappedXMLInputFactory inputFactory = new MappedXMLInputFactory(XMLToJSNNamespaceMap);
        
        String jsonString = "";
        
        if (jsonResponseWrappedInAContainerObject) {        	
        	jsonString = "{" + localName + ":" + this.getJSONString();
        } else {
        	jsonString = "{" + localName + ":" + this.getJSONString() + "}";
        }
        
        return inputFactory.createXMLStreamReader(new JSONTokener(jsonString));
    }

}
