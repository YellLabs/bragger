import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName         = "petstore-client"
	val appVersion      = "1.2.3-SNAPSHOT"
	
	val appDependencies = Seq(

	    // TODO sort out jettison-patched.jar	    
	    //"com.hibu" % "bragger-core" % "1.2.3-SNAPSHOT", 
		
	    javaCore,
		javaJdbc,
		javaEbean
	)

	val main = play.Project(appName, appVersion, appDependencies).settings(
	  
		// adding to the project sources the autogenerated code for calling the petstore api 
		unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "codegen" / "src" ),		
		// TODO generated test sources???

		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",
		
		// sbt eclipse
		EclipseKeys.withSource in ThisBuild := true
	)
	
}
