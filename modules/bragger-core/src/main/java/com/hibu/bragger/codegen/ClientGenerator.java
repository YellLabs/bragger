package com.hibu.bragger.codegen;

import java.io.File;

/**
 * this interface has to be implemented by different client generators
 * depending on the code generation tool used.
 * e.g. an AxisClientGenerator would use asixs2 wsdl2java tool, 
 *      a Play2ClientGenerator would generate play2 client code.  
 *      
 * the method returns a sequence of source files that will be added 
 * by the sbt plugin to the managed sources
 * 
 * In theory all the implementations of the ClientGenerator interface should NOT depend
 * on the build system (sbt, maven) so that they can be reused across different build systems.
 * It should depend only on the framework used to generate the api client code (e.g. Axis2 wsdl2gen)
 * 
 * TODO this interface should be moved out of the sbt plugin to stay in a stand alone package, like bragger-clientgen (bragger-core may be ok as well)
 * And both the sbt/mavn-plugin modules and the interface implementation modules (e.g. bragger-clientgen-axis2 ) would depend on it.
 * 
 * @author paolo
 * 
 */
public interface ClientGenerator {

	public Iterable<File> generate();
	
}
