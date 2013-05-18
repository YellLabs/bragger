import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-client"
	val appVersion = "1.3.2"

	val appDependencies = Seq()
	
	lazy val root = Project(id = appName, 
		base = file("."),
		settings = Project.defaultSettings ++ Seq(
		    
			libraryDependencies ++= Seq(
				"com.hibu" % "bragger-commons" % appVersion,
			    
				// ====== dependencies to use the client stubs auto generated form wsdl ======
        		"axis" % "axis-wsdl4j" % "1.5.1" excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
        		"org.apache.axis2" % "axis2-adb" % "1.6.2" excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
        		"org.apache.axis2" % "axis2-jaxbri" % "1.6.2" excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
        		"org.apache.axis2" % "axis2-codegen" % "1.6.2"excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
        		"org.apache.axis2" % "axis2-java2wsdl" % "1.6.2" excludeAll(
        			ExclusionRule(organization = "org.apache.geronimo.specs", name= "geronimo-javamail_1.4_spec"),
        			ExclusionRule(organization = "javax.servlet", name = "servlet-api")
        		),
        		"org.apache.axis2" % "axis2-json" % "1.6.2" exclude("org.codehaus.jettison", "jettison"),
        		// using patched jettison: 
        		// see http://www.marcusschiesser.de/2009/01/building-a-json-web-service-with-java-and-axis2/
        		// see http://markmail.org/message/cu2tw43qnrqgqgwp
        		// see http://dktiwari-hbti.blogspot.co.uk/2012/07/java-web-services.html
        		"org.codehaus.jettison" % "jettison" % "1.3.3.hibu",
        		"backport-util-concurrent" % "backport-util-concurrent" % "3.1"
			),
			
			organization in ThisBuild := "com.hibu",
			version in ThisBuild := appVersion,
			
			publishArtifact in (Test, packageSrc) := false,
			publishMavenStyle in ThisBuild := true,
			
			scalaVersion := "2.10.0",
			autoScalaLibrary := false,
			crossPaths := false,		
			testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
			
			// bragger repo
			resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
			// easywsdl 
			resolvers in ThisBuild += "Petalslink Maven" at "http://maven.petalslink.com/public",
			// local maven repo
			//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
			
			// sbteclipse settings
			EclipseKeys.executionEnvironment in ThisBuild := Some(EclipseExecutionEnvironment.JavaSE16),
			EclipseKeys.projectFlavor in ThisBuild := EclipseProjectFlavor.Java,
			EclipseKeys.skipParents in ThisBuild := false,
			EclipseKeys.withSource in ThisBuild := false
		)
	)

}
