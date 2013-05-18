import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-commons"
	val appVersion = "1.3.2"

	val appDependencies = Seq()
	
	lazy val root = Project(id = appName, 
		base = file("."),
		settings = Project.defaultSettings ++ Seq(
		    
			libraryDependencies ++= Seq(
        		"commons-io" % "commons-io" % "2.4",
        		"org.slf4j" % "slf4j-api" % "1.7.2"
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
