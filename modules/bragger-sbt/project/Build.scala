import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-sbt"
	val appVersion = "1.3.1-SNAPSHOT"

	val appDependencies = Seq(
		"com.hibu" % "bragger-client" % appVersion//, //excludeAll(ExclusionRule(organization = "javax.servlet", name = "servlet-api")),
	)
	
	lazy val root = Project(
		id = appName, 
		base = file("."),
		settings = Project.defaultSettings ++ Seq(
			
			sbtPlugin := true,
			
			libraryDependencies ++= appDependencies,
			
			organization in ThisBuild := "com.hibu",
			version in ThisBuild := appVersion,
			
			publishArtifact in (Compile, packageDoc) := false,
			publishArtifact in (Compile, packageSrc) := false,
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
		//braggerClient
	)
	
	//lazy val braggerClient = ProjectRef(file("../bragger-client"), "bragger-client")
	
}
