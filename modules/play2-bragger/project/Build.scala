import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

	val appName    = "play2-bragger"
	val appVersion = "1.2.3-SNAPSHOT"

	val appDependencies = Seq(
		//"com.hibu" % "bragger-core" % appVersion exclude("com.wordnik", "swagger-core_2.9.1"),
		//"com.wordnik" %% "swagger-play2" % "1.2.1.hibu-SNAPSHOT"
	)
	
	lazy val swaggerPlay2 = ProjectRef(file("../../../swagger-core/modules/swagger-play2"), "swagger-play2")
	
	lazy val braggerCore = ProjectRef(file("../bragger-core"), "bragger-core")
	
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(

// 		needed only for play 2.0.x as there's a bug in sbt 0.11 on the exclude() function
//		ivyXML :=
//			<dependency org="com.wordnik" name="swagger-play2_2.9.1" rev="1.2.0">
//				<exclude org="play" module="play_2.9.1"/>
//			</dependency>,
	
		organization in ThisBuild := "com.hibu",
		publishArtifact in (Compile, packageDoc) := false,
		publishMavenStyle in ThisBuild := true,
		testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
		
		// local maven
		//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
		// custom modules
		resolvers += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public", 
	
		// sbteclipse settings
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := false
	
	).dependsOn(
		swaggerPlay2,
		braggerCore
	).aggregate(
		swaggerPlay2,
		braggerCore
	)
	
}
