package com.hibu.bragger.wsdl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class CustomPrefixMapper extends NamespacePrefixMapper {

	public Map<String, String> predefinedNamespaces = new HashMap<String, String>();

	public CustomPrefixMapper() {
	}

	public CustomPrefixMapper(String[] customNamespaces) {
		try {

			for (int i = 0; i < customNamespaces.length; i++) {

				String prefix = customNamespaces[i++];
				String namespace = customNamespaces[i];

				this.predefinedNamespaces.put(namespace, prefix);
			}
		} catch (Exception e) {
			System.out.println("Error while initialising custom namespaces. Using default namespaces.");
			this.predefinedNamespaces.clear();
		}
	}

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {

		if (namespaceUri.equals("http://schemas.xmlsoap.org/wsdl/")) {
			return "wsdl11";
		} else if (namespaceUri.equals("http://schemas.xmlsoap.org/wsdl/soap12/")) {
			return "soap12";
		} else if (namespaceUri.equals("http://schemas.xmlsoap.org/wsdl/soap/")) {
			return "soap";
		} else if (namespaceUri.equals("http://schemas.xmlsoap.org/wsdl/http/")) {
			return "whttp";
		} else if (namespaceUri.equals("http://schemas.xmlsoap.org/wsdl/mime/")) {
			return "wmime";
		} else if (namespaceUri.equals("http://www.w3.org/2001/XMLSchema")) {
			return "xs";
		} else if (this.predefinedNamespaces.containsKey(namespaceUri)) {
			return this.predefinedNamespaces.get(namespaceUri);
		}

		return suggestion;
	}

	@Override
	public String[] getPreDeclaredNamespaceUris() {

		String[] r = new String[this.predefinedNamespaces.size()];
		this.predefinedNamespaces.keySet().toArray(r);
		return r;

	}

	@Override
	public String[] getPreDeclaredNamespaceUris2() {

		String[] custNS = new String[this.predefinedNamespaces.size() * 2];

		Iterator<String> it = this.predefinedNamespaces.keySet().iterator();

		int i = 0;
		while (it.hasNext()) {
			String ns = it.next();
			String prefix = this.predefinedNamespaces.get(ns);

			custNS[i++] = prefix;
			custNS[i++] = ns;
		}
		return custNS;
	}
	
}
