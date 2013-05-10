import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-core"
	val appVersion = "1.3.1-SNAPSHOT"

	val appDependencies = Seq()
	
	lazy val root = Project(id = appName, 
		base = file("."),
		settings = Project.defaultSettings ++ Seq(
		    
			libraryDependencies ++= Seq(
				"com.hibu" % "bragger-commons" % appVersion,
				"com.wordnik" % "swagger-core_2.9.1" % "1.2.2-SNAPSHOT" excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
				"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3" excludeAll(
				    ExclusionRule(organization = "javax.servlet", name = "servlet-api")
				),
				"com.ebmwebsourcing.easycommons" % "easycommons.xml" % "1.1" // even though it's required for easywsdl-wsdl to function, it's not part of its dependency tree
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
			EclipseKeys.withSource in ThisBuild := true
			
		)
	).dependsOn(
	    //braggerCommons
	)
	
	//lazy val braggerCommons = ProjectRef(file("../bragger-commons"), "bragger-commons")

}
