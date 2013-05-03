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
 * 
 * @author paolo
 * 
 */
public interface ClientGenerator {

	public Iterable<File> generate();
	
}
