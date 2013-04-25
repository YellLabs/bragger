package com.hibu.bragger.sbt;

import sbt._
import sbt.Keys._
import java.io.File
import com.hibu.bragger.codegen.ClientGenerator
import com.hibu.bragger.codegen.axis2.AxisClientCodeGenerator
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object BraggerSbtPlugin extends sbt.Plugin {
  
    object BraggerKeys {
      
		val braggerGenerate = TaskKey[Seq[File]]("bragger-generate", "this task trigger the code generation")
    	val braggerClients = SettingKey[Seq[Api]]("bragger-clients", "setting to configure the bragger-generate task. each Api object contains the data required to create each client")
    	
    	case class Api(wsdlUrl: String, targetPackage: String) {
			//override def toString(): String = wsdlUrl
		}
    }

    import BraggerKeys._

    lazy val braggerSettingsCompile: Seq[Setting[_]] = inConfig(Compile)(baseBraggerSettings) ++ Seq(
        unmanagedSourceDirectories in Compile <+= target (_ / "bragger" / "src")
        //sourceGenerators in Compile <+= braggerGenerate in Compile
    )

    lazy val braggerSettingsTest: Seq[Setting[_]] = inConfig(Test)(baseBraggerSettings) ++ Seq(
        unmanagedSourceDirectories in Test <+= target (_ / "bragger" / "test")
        //sourceGenerators in Test <+= braggerGenerate in Compile
    )
    
    val baseBraggerSettings: Seq[Setting[_]] = Seq(
        
        // bragger-generate task
        braggerGenerate <<= (streams, braggerClients in braggerGenerate, target) map { (s, clients, t) =>
        	s.log.info("---" + scala.Console.CYAN_B + "GENERATING API CLIENTS")
          	
            var files = Seq[File]() 
            clients foreach(c => {  
              
            	s.log.info("--- Generating Api Client from " + c.wsdlUrl)
 
				// TODO find an implementation dynamically, maybe use a settings to configure and manage this
				val clientGenerator = new AxisClientCodeGenerator(c.wsdlUrl, c.targetPackage, (t / "bragger"))
            	val generatedSourceFiles = clientGenerator.generate()
            	
            	s.log.info("--- Adding " + generatedSourceFiles.size + " files to build classpath")
				files = files ++ generatedSourceFiles // TODO make this functional (use fold)!!!

				s.log.info("--- Client generated to:")
				s.log.info("    output folder " + t / "bragger")
				s.log.info("    output package " + c.targetPackage)
				
             })
            
            s.log.info("---" + scala.Console.CYAN_B + "API CLIENTS GENERATED")
        	files
        }
    )
    
    // automatically importing all bragger settings to all projects using this plugin
    override def settings: Seq[Setting[_]] = Seq(
        
        // default value for when bragger-clients is not specified by the build author
        braggerClients in Global := Seq()
        
        // if the generated sources are added to managedSourceDirectories rather than unmanagedSourceDirectories
        // use this to have them added in the eclipse project's sources 
        //EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed
        
    ) ++ braggerSettingsCompile ++ braggerSettingsTest
    
}
