import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName    = "petstore-java-play2"
	val appVersion = "1.3.0-SNAPSHOT"

	val appDependencies = Seq(
		"com.hibu" %% "bragger-play2" % appVersion,
		javaCore
	)
		
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(
                
		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",

		// sbt eclipse
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := false
		
	).dependsOn(
	    //play2Bragger
	).aggregate(
	    //play2Bragger
	)
	
	lazy val play2Bragger = ProjectRef(file("../../modules/bragger-play2"), "bragger-play2")
	
}
