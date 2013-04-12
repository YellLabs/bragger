import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName = "petstore-java-play2"
	val appVersion = "1.2.3-SNAPSHOT"

	val appDependencies: Seq[sbt.ModuleID] = Seq(
		//"com.hibu" %% "play2-bragger" % appVersion
	)
	
	lazy val play2Bragger = ProjectRef(file("../../modules/play2-bragger"), "play2-bragger")
		
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(
                
		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",
		
		// with these lines, the 'play eclipse' command adds automatically the source folders to the porject
		// remove these when getting the linked modules as managed dependencies
		// put here an entry for each element specified in the dependsOn (directly or recursively across all the subprojects)
		//unmanagedSourceDirectories in Compile <+= baseDirectory( _ / ".." / ".." / "modules" / "play2-bragger" / "app"),
		//unmanagedSourceDirectories in Compile += file("../../modules/play2-bragger/app"),
		//unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "linked-modules" / "swagger-play2" / "app"),
		//unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "linked-modules" / "bragger-core" / "src" / "main" / "java" ),
		
		// sbt eclipse
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := true
		
	).dependsOn(
	    play2Bragger
	).aggregate(
	    play2Bragger
	)
		
}
