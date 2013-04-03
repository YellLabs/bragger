import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName         = "bragger-core"
	val appVersion      = "1.1.0"

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
				
				"com.wordnik" % "swagger-core_2.9.1" % "1.2.2.hibu-SNAPSHOT",
				"com.wordnik" % "swagger-annotations_2.9.1" % "1.2.2-SNAPSHOT"

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
