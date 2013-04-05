export AXIS2_HOME=/Users/paolo/dev/axis/axis2-1.6.2/
export JAVA_HOME=/Library/Java/Home

# see http://axis.apache.org/axis2/java/core/tools/CodegenToolReference.html for wsdl2java command line options reference

wsdl2java.sh --wsdl-version 2.0 --databinding-method jaxbri --package com.hibu.api.petservice --serverside-interface --unpack-classes --test-case --over-ride -uri http://localhost:9000/api-docs.wsdl/pet


