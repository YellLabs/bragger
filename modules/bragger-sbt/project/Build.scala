import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-sbt"
	val appVersion = "1.3.0-SNAPSHOT"

	val appDependencies = Seq(
		"com.hibu" % "bragger-core" % appVersion
	)
	
	lazy val braggerCore = ProjectRef(file("../bragger-core"), "bragger-core")
	
	lazy val root = Project(
		id = appName, 
		base = file("."),
		settings = Project.defaultSettings ++ Seq(
			
		    sbtPlugin := true,
		    
			libraryDependencies ++= appDependencies,
			
			organization in ThisBuild := "com.hibu",
			version in ThisBuild := appVersion,
			
			publishArtifact in (Compile, packageDoc) := false,
			publishArtifact in (Compile, packageSrc) := true,
			publishArtifact in (Test, packageSrc) := false,
			publishMavenStyle in ThisBuild := false,
			
			//autoScalaLibrary := false,
			//crossPaths := false,
			//testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
			
			// bragger repo
			resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
			// easywsdl 
			resolvers in ThisBuild += "Petalslink Maven" at "http://maven.petalslink.com/public",
			
			// sbteclipse settings
			EclipseKeys.skipParents in ThisBuild := false,
			EclipseKeys.withSource in ThisBuild := false
		)
	).dependsOn(
	    //braggerCore
	)
	
}
