import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName         = "bragger-core"
	val appVersion      = "1.0.0-SNAPSHOT"

	val appDependencies = Seq()
	
	lazy val root = Project(
		
		id = appName, 
		
		base = file("."),
		
		settings = Project.defaultSettings ++ Seq(
				
			libraryDependencies ++= Seq(
				"commons-io" % "commons-io" % "2.4",

				// easy-wsdl libs
				"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3",
				"com.ebmwebsourcing.easycommons" % "easycommons.xml" % "1.1", // even though it's required for easywsdl-wsdl to function, it's not part of its dependency tree
				
				// swagger-core and swagger-annotations are "unmanaged" (copied inside libs foldes)
				// so their dependencies need to be declared
				"commons-lang" % "commons-lang" % "2.4",
				"org.slf4j" % "slf4j-api" % "1.6.3",
				"com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.0",
				"com.fasterxml.jackson.core" % "jackson-annotations" % "2.0.4",
				"com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.0.0",
				"joda-time" % "joda-time" % "2.2",
				"org.joda" % "joda-convert" % "1.2"
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
