import sbt._
import Keys._
import play.Project

object ApplicationBuild extends Build {

  val appName = "petstore-java-play2"
  val appVersion = "1.2.0"

  val appDependencies: Seq[sbt.ModuleID] = Seq(
      "com.hibu" % "play2-bragger" % "1.0.0" excludeAll(
          // avoid problems due to incompatibility between scala 2.9 and scala 2.10
	      ExclusionRule(organization = "com.wordnik", name = "swagger-core_2.9.1"),
	      ExclusionRule(organization = "com.wordnik", name = "swagger-annotations_2.9.1")
	  )
  )

  val main = Project(appName, appVersion, appDependencies).settings(
      // bragger repo
      resolvers += "github pages repo" at "http://yelllabs.github.com/bragger",
      // easywsdl      
      resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public", 
      // typesafe
      resolvers in ThisBuild += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      // public maven repos
      resolvers in ThisBuild += "repo1.maven.org" at "http://repo1.maven.org/maven2", 
      resolvers in ThisBuild += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots", 
      resolvers in ThisBuild += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases", 
      resolvers in ThisBuild += "java-net" at "http://download.java.net/maven/2"
    )
}
