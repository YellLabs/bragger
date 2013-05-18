import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin.{EclipseCreateSrc, EclipseKeys}
import com.hibu.bragger.sbt.BraggerSbtPlugin.BraggerKeys._

object ApplicationBuild extends Build {

	val appName    = "petstore-client"
	val appVersion = "1.3.2"
	
	val appDependencies = Seq(
		"com.hibu" % "bragger-client" % appVersion, 
		javaCore
	)
	
	//override lazy val projects = Seq(root)
	
	lazy val root = play.Project(appName, appVersion, appDependencies).settings(

		braggerClients in braggerGenerate := Seq(
			Api("http://localhost:9000/docs/api-docs.wsdl/pet"  , "com.hibu.apis.petstore.clients.pet"),
			Api("http://localhost:9000/docs/api-docs.wsdl/user" , "com.hibu.apis.petstore.clients.user"),
			Api("http://localhost:9000/docs/api-docs.wsdl/store", "com.hibu.apis.petstore.clients.store")
		),
		
		//sourceGenerators in Test <+= braggerGenerate in Compile,

		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",

		// sbt eclipse
		//EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed,
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := false
		
	).dependsOn(
		//braggerClient
	)
	
	//lazy val braggerClient = ProjectRef(file("../../../../modules/bragger-client"), "bragger-client")
	
}
