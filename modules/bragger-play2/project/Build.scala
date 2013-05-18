import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "bragger-play2"
	val appVersion = "1.3.2"

	val appDependencies = Seq(
		"com.hibu" % "bragger-core" % appVersion exclude("com.wordnik", "swagger-core_2.9.1"),
		"com.wordnik" %% "swagger-play2" % "1.2.1.hibu3"
	)

	lazy val main = play.Project(appName, appVersion, appDependencies).settings(
	
		organization in ThisBuild := "com.hibu",
		publishArtifact in (Compile, packageDoc) := false,
		publishMavenStyle in ThisBuild := true,
		testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
		
		// custom modules
		resolvers += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public", 
	
		// sbteclipse settings
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := false
	
	).dependsOn(
		//swaggerPlay2 , 
		//braggerCore
	)

	//lazy val braggerCore = ProjectRef(file("../bragger-core"), "bragger-core")
	
	//lazy val swaggerPlay2 = ProjectRef(file("../../../swagger-core/modules/swagger-play2"), "swagger-play2")
}
