import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ApplicationBuild extends Build {

  val appName         = "play2-bragger"
  val appVersion      = "1.2.1"

  val appDependencies = Seq(
    "com.hibu" % "bragger-core" % "1.1.0" exclude("com.wordnik", "swagger-core_2.9.1"),
    "com.wordnik" %% "swagger-play2" % "1.2.1.hibu-SNAPSHOT",
    "org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3"
  )

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(

// needed only for play 2.0.x
//    ivyXML :=
//      <dependency org="com.wordnik" name="swagger-play2_2.9.1" rev="1.2.0">
//        <exclude org="play" module="play_2.9.1"/>
//      </dependency>,

	publishArtifact in (Compile, packageDoc) := false,
	
	testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
	
	organization in ThisBuild := "com.hibu",
	
	publishMavenStyle in ThisBuild := true,
	
	// don't publish parent project
	//publishArtifact := false,
	
    // local maven
    //resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
    
	// custom modules
	resolvers += "github pages repo" at "http://yelllabs.github.com/bragger",
	
    // easywsdl
    resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public", 
    
    // typesafe
    resolvers in ThisBuild += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",

    // public maven repos
    resolvers in ThisBuild += "repo1.maven.org" at "http://repo1.maven.org/maven2", 
    resolvers in ThisBuild += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots", 
    resolvers in ThisBuild += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases", 
    resolvers in ThisBuild += "java-net" at "http://download.java.net/maven/2",

	// sbteclipse settings
	EclipseKeys.executionEnvironment in ThisBuild := Some(EclipseExecutionEnvironment.JavaSE16),
	EclipseKeys.projectFlavor in ThisBuild := EclipseProjectFlavor.Java,
	EclipseKeys.skipParents in ThisBuild := false,
	EclipseKeys.withSource in ThisBuild := true
	
  )

}
