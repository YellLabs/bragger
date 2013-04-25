import sbt._

object PluginDef extends Build {
   
	override lazy val projects = Seq(root)
	
	lazy val root = Project("petstore-client-plugins", file(".")) dependsOn(braggerSbt)
	
	lazy val braggerSbt = ProjectRef(file("../../../../../modules/bragger-sbt"), "bragger-sbt")
	
}