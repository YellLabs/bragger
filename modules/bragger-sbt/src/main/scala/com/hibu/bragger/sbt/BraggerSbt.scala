package com.hibu.bragger.sbt;

import sbt._
import sbt.Keys._
import java.io.File
import com.hibu.bragger.codegen.ClientGenerator
import com.hibu.bragger.codegen.axis2.AxisClientCodeGenerator
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object BraggerSbtPlugin extends sbt.Plugin {

	case class Api(wsdlUrl: String, targetPackage: String, targetFolder: String) {
	  override def toString(): String = wsdlUrl
	}
	
	object BraggerClientGenerator {
		def apply(clientConfig: Api, logger: sbt.Logger): Seq[File] = {
			// call ClientGenerator implementation
			// maybe use a settings to configure and manage this
		    // TODO find an implementation dynamically
			val clientGenerator: ClientGenerator = new AxisClientCodeGenerator(clientConfig.wsdlUrl, clientConfig.targetPackage, clientConfig.targetFolder)
			clientGenerator.generate()
		}
	}
  
    object BraggerKeys {
		val braggerGenerate = TaskKey[Seq[File]]("bragger-generate")
    	val braggerClients  = SettingKey[Seq[Api]]("bragger-clients")
    }

    import BraggerKeys._
        
    lazy val braggerSettings: Seq[Setting[_]] = inConfig(Compile)(baseBraggerSettings)
    
    val baseBraggerSettings: Seq[Setting[_]] = Seq(
        braggerGenerate <<= (streams, braggerClients) map { (s, clients) =>
          	val files = List[File]() // TODO make this functional!!! 
            clients foreach(c => {
              s.log.info(scala.Console.CYAN_B + "================ Generating Api Client from " + c)
              files ++ BraggerClientGenerator(c, s.log)
             })
        	files
        }
    )
    
    // automatically importing all bragger settings to all projects using this plugin
    override def settings: Seq[Setting[_]] = braggerSettings
    
}
