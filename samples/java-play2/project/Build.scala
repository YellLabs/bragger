import sbt._
import Keys._
import play.Project
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName = "petstore-java-play2"
	val appVersion = "1.3.0"

	val appDependencies: Seq[sbt.ModuleID] = Seq(
		"com.hibu" %% "play2-bragger" % "1.2.1"
	)

	lazy val play2Bragger = play.Project("play2-bragger", "1.2.0", 
		Seq(
			"com.hibu" % "bragger-core" % "1.1.0" exclude("com.wordnik", "swagger-core_2.9.1"),
			"com.wordnik" %% "swagger-play2" % "1.2.1.hibu-SNAPSHOT",
			"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3"
		), 
		path = file("./linked-modules/play2-bragger")
	).settings(
		EclipseKeys.skipProject := true
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
	)

}
