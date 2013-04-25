package com.hibu.bragger.codegen.axis2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.axis2.util.CommandLineOptionConstants;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.codegen.CodeGenerationEngine;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.util.WSDL2JavaOptionsValidator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.hibu.bragger.codegen.ClientGenerator;

/**
 * this class but in general all the implementations of the ClientGenerator interface 
 * should be written to be reused in other plugins for build systems like maven
 * (maybe in maven there's already an axis wsdl2java plugin!! TODO find out!) 
 * 
 * @author paolo
 *
 */
public class AxisClientCodeGenerator implements ClientGenerator {
	
	private String wsdlUrl;
	private String targetPackage;
	private File targetFolder;
	
	public AxisClientCodeGenerator(String wsdlUrl, String targetPackage, File targetFolder) {
		super();
		this.wsdlUrl = wsdlUrl;
		this.targetPackage = targetPackage;
		this.targetFolder = targetFolder;
	}

	@Override
	public Collection<File> generate() {
		
		try {
			
			// first generate the command line argument to invoke wsdl2java engine
			String[] args = toCommandLineOptions(wsdlUrl, targetPackage, targetFolder);
			
			CommandLineOptionParser commandLineOptionParser = new CommandLineOptionParser(args);
	        	
			if (isOptionsValid(commandLineOptionParser)) {
	        	
	        	CodeGenerationEngine engine = new CodeGenerationEngine(commandLineOptionParser);
	        	
	        	// trigger the generation using axis wsdl2java
	        	engine.generate();

	        	// code is now generated
	        	// now i need to fetch all generated source files and return them
	        	// (so that they can be added to the build in sbt)
	        	Collection<File> files = FileUtils.listFiles(targetFolder, FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
				
	        	return files;
	            
	        } else {
	        	// TODO log warning to the building console
	            return new ArrayList<File>();
	        }
			
		} catch (CodeGenerationException e) {
			e.printStackTrace();
			return new ArrayList<File>();
		}
	}

	// ========================================================================
	
	/**
	 * generate the command line options to be sent as the input of the wsdl2java engine
	 * based on the input coming from the sbt/maven plugin.
	 * Many wsdl2java options are hard coded as they never change, others
	 * depend on what comes form the sbt/maven build configuration 
	 * 
	 * An Example of invocation to the wsdl2java tool:
	 * wsdl2java.sh \
	 * --wsdl-version 2.0 \
	 * --databinding-method jaxbri \
	 * --package com.hibu.api.petservice \
	 * --serverside-interface --sync --unpack-classes --test-case \
	 * --over-ride -uri http://localhost:9000/api-docs.wsdl/pet
	 * 
	 * see http://axis.apache.org/axis2/java/core/tools/CodegenToolReference.html for command line options reference
	 * 
	 * @param wsdlUrl
	 * @param targetPackage
	 * @param targetFolder
	 * @return
	 */
	private String[] toCommandLineOptions(String wsdlUrl, String targetPackage, File targetFolder) {
		// TODO use CommandLineOptionConstants.WSDL2JavaConstants here
		return new String[]{
				"--wsdl-version", "2.0", 
				"--databinding-method", "jaxbri", 
				"--package", targetPackage, 
				"--serverside-interface", 
				"--sync", 
				"--unpack-classes", 
				//"--test-case",
				"--over-ride", 
				"--noBuildXML",
				
				//TODO think about multiple clients scenario! does a different source folder for each api make sense? maybe named after a serviceName param
				// mmmm maybe not... maybe different clients go to the same folder with different packages otherwise
				// there's no way of adding each folder to the sourceManaged. i mean, how can you add to the sourceManaged settings inside the bragger-generate task code??
				"--output", targetFolder.getAbsolutePath(), //"target/scala-2.10/src_managed/bragger", 
				
				"--resource-folder", "resources",
				"--source-folder", "src",
				
				"-uri", wsdlUrl
		};
	}
	
    private static boolean isOptionsValid(CommandLineOptionParser parser) {
        boolean isValid = true;
        if (parser.getInvalidOptions(new WSDL2JavaOptionsValidator()).size() > 0)
            isValid = false;
        if (null == parser.getAllOptions().get(CommandLineOptionConstants.WSDL2JavaConstants.WSDL_LOCATION_URI_OPTION))
            isValid = false;
        return isValid;
    }
  
}
