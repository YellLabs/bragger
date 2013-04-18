package com.hibu.bragger.codegen.axis2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis2.util.CommandLineOptionConstants;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.codegen.CodeGenerationEngine;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.WSDL2JavaOptionsValidator;

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
	private String targetFolder;
	
	public AxisClientCodeGenerator(String wsdlUrl, String targetPackage, String targetFolder) {
		super();
		this.wsdlUrl = wsdlUrl;
		this.targetPackage = targetPackage;
		this.targetFolder = targetFolder;
	}

	@Override
	public List<File> generate() {
		
		try {
			
			// first generate the command line argument to invoke wsdl2java engine
			String[] args = toCommandLineOptions(wsdlUrl, targetPackage, targetFolder);
			
			CommandLineOptionParser commandLineOptionParser = new CommandLineOptionParser(args);
			
			CodeGenerationEngine engine = new CodeGenerationEngine(commandLineOptionParser);
			
	        if (isOptionsValid(commandLineOptionParser)){
	            
	        	// trigger the generation using axis generation engine
	        	engine.generate();
	            
	            // code is now generated 
	            // i can return the list of generated source files 
	            // so that they can be added to the build
	            return getGeneratedSourceFiles(targetFolder);
	            
	        } else {
	        	// TODO find a way to propagate this messages to sbt/maven-plugin so that they can be printed
	            printUsage();
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
	private String[] toCommandLineOptions(String wsdlUrl, String targetPackage, String targetFolder) {
		// TODO use CommandLineOptionConstants.WSDL2JavaConstants here
		return new String[]{
				"--wsdl-version", "2.0", 
				"--databinding-method", "jaxbri", 
				"--package", targetPackage, 
				"--serverside-interface", 
				"--sync", 
				"--unpack-classes", 
				"--test-case",
				"--over-ride", 
				"--output", targetFolder, 
				"-uri", wsdlUrl
		};
	}

	/**
	 * fetch all source files generated from the engine
	 * this could be made public if we wanted to maintain state
	 * and let the client code access the files even after the code generation
	 * 
	 * TODO implement
	 * 
	 * @param targetFolder
	 * @return
	 */
	private List<File> getGeneratedSourceFiles(String targetFolder) {
		// TODO implement
		return new ArrayList<File>();
	}
	
    private static void printUsage() {

        System.out.println(CodegenMessages.getMessage("wsdl2code.arg"));
        System.out.println(CodegenMessages.getMessage("wsdl2code.arg1"));
        for (int i = 2; i <= 49; i++) {
            System.out.println("  " + CodegenMessages.getMessage("wsdl2code.arg" + i));
        }
    }

    private static boolean isOptionsValid(CommandLineOptionParser parser) {
        boolean isValid = true;
        if (parser.getInvalidOptions(new WSDL2JavaOptionsValidator()).size() > 0){
            isValid = false;
        }
        if (null == parser.getAllOptions().get(
                        CommandLineOptionConstants.WSDL2JavaConstants.WSDL_LOCATION_URI_OPTION)){
            isValid = false;
        }
        return isValid;
    }
  
}
