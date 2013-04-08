import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName         = "bragger-core"
	val appVersion      = "1.2.3-SNAPSHOT"

	val appDependencies = Seq()
	
	lazy val root = Project(
		
		id = appName, 
		
		base = file("."),
		
		settings = Project.defaultSettings ++ Seq(
				
			libraryDependencies ++= Seq(

	            // commons
        		"commons-io" % "commons-io" % "2.4",
        		"javax.mail" % "mail" % "1.4",
        		
				// dependencies to use the client stubs auto generated form wsdl 
        		"axis" % "axis-wsdl4j" % "1.5.1",
        		"org.apache.axis2" % "axis2" % "1.6.2",
        		"org.apache.axis2" % "axis2-java2wsdl" % "1.6.2" excludeAll(
        			ExclusionRule(organization = "org.apache.geronimo.specs"),
        			ExclusionRule(organization = "javax.servlet")
        		),
        		
        		// using patched jettison in /lib folder
        		"org.apache.axis2" % "axis2-json" % "1.6.2" exclude("org.codehaus.jettison", "jettison"),
        		//"org.codehaus.jettison" % "jettison" % "1.2",
        		
        		"backport-util-concurrent" % "backport-util-concurrent" % "3.1",
        		
				// easy-wsdl libs
				"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3",
				"com.ebmwebsourcing.easycommons" % "easycommons.xml" % "1.1", // even though it's required for easywsdl-wsdl to function, it's not part of its dependency tree
				
				// swagger
				"com.wordnik" % "swagger-core_2.9.1" % "1.2.2-SNAPSHOT", // exclude("org.slf4j", "slf4j-log4j12") // 1.6.3

			),
			
			publishArtifact in (Compile, packageDoc) := false,
			
			autoScalaLibrary := false,
			
			crossPaths := false,
			
			testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
			
			organization in ThisBuild := "com.hibu",
			
			version in ThisBuild := appVersion,
			
			publishMavenStyle in ThisBuild := true,
			
			// don't publish parent project
			//publishArtifact := false,
			
			//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
			
			// bragger repo
			resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
      
			// easywsdl 
			resolvers in ThisBuild += "Petalslink Maven" at "http://maven.petalslink.com/public",
			
			// sbteclipse settings
			EclipseKeys.executionEnvironment in ThisBuild := Some(EclipseExecutionEnvironment.JavaSE16),
			EclipseKeys.projectFlavor in ThisBuild := EclipseProjectFlavor.Java,
			EclipseKeys.skipParents in ThisBuild := false,
			EclipseKeys.withSource in ThisBuild := true
			
		)
	)
}
