import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName = "petstore-java-play2"
	val appVersion = "1.2.2"

	val appDependencies: Seq[sbt.ModuleID] = Seq(
		"com.hibu" %% "play2-bragger" % "1.2.2"
	)

	lazy val braggerCoreSubProject = Project(
	    id = "bragger-core", 
	    base = file("./linked-modules/bragger-core"), 
	    settings = Project.defaultSettings ++ Seq(
	        libraryDependencies ++= Seq(
				"commons-io" % "commons-io" % "2.4",
				"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3",
				"com.ebmwebsourcing.easycommons" % "easycommons.xml" % "1.1",
				"com.wordnik" % "swagger-core_2.10.0" % "1.2.0"
	        ),
			publishArtifact in (Compile, packageDoc) := false,
			autoScalaLibrary := false,
			crossPaths := false,
			testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
	    )
	)
	
	lazy val play2Bragger = play.Project("play2-bragger", "1.2.2", 
		Seq(
			"com.hibu" % "bragger-core" % "1.2.2" exclude("com.wordnik", "swagger-core_2.9.1"), //comment out this line if dependsOn include braggerCoreSubProject
			"com.wordnik" %% "swagger-play2" % "1.2.1.hibu-SNAPSHOT",
			"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3"
			//"org.slf4j" % "slf4j-api" % "1.7.2"
		), 
		path = file("./linked-modules/play2-bragger")
	).settings(
		EclipseKeys.skipProject := true
	).dependsOn(
	    //braggerCoreSubProject
	).aggregate(
	    //braggerCoreSubProject
	)
	
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(
		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",
		// typesafe
		resolvers in ThisBuild += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
		// public maven repos
		resolvers in ThisBuild += "repo1.maven.org" at "http://repo1.maven.org/maven2",
		resolvers in ThisBuild += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
		resolvers in ThisBuild += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases",
		resolvers in ThisBuild += "java-net" at "http://download.java.net/maven/2"
	).dependsOn(
	    //play2Bragger
	).aggregate(
	    //play2Bragger
	)

}
