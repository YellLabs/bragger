import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName = "petstore-java-play2"
	val appVersion = "1.2.3-SNAPSHOT"

	val appDependencies: Seq[sbt.ModuleID] = Seq(
		//"com.hibu" %% "play2-bragger" % appVersion
	)

	lazy val braggerCoreSubProject = Project(id = "bragger-core", 
	    base = file("./linked-modules/bragger-core"), 
	    settings = Project.defaultSettings ++ Seq(
	        libraryDependencies ++= Seq(
	            
	            // commons
        		"commons-io" % "commons-io" % "2.4",
        		"javax.mail" % "mail" % "1.4",
        		
        		// axis
        		"axis" % "axis-wsdl4j" % "1.5.1",
        		"org.apache.axis2" % "axis2" % "1.6.2",
        		"org.apache.axis2" % "axis2-java2wsdl" % "1.6.2" excludeAll(
        			ExclusionRule(organization = "org.apache.geronimo.specs"),
        			ExclusionRule(organization = "javax.servlet")
        		),
        		
        		// using patched jettison in /lib folder
        		"org.apache.axis2" % "axis2-json" % "1.6.2" exclude("org.codehaus.jettison", "jettison"),
        		//"org.codehaus.jettison" % "jettison" % "1.2",
        		
        		"backport-util-concurrent" % "backport-util-concurrent" % "3.1",
        		
				// easy-wsdl libs
				"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3",
				"com.ebmwebsourcing.easycommons" % "easycommons.xml" % "1.1",
				
				// swagger
				"com.wordnik" % "swagger-core_2.10.0" % "1.2.0" exclude("org.slf4j", "slf4j-log4j12") // 1.6.3
	        ),
			publishArtifact in (Compile, packageDoc) := false,
			autoScalaLibrary := false,
			crossPaths := false,
			testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
			
			// avoid eclipsify the subproject if aggregate is used on the parent project
			EclipseKeys.skipProject := true
	    )
	)
	
	lazy val swaggerPlay2 = play.Project("swagger-play2", appVersion, 
		Seq(
		    "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.0",
		    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.0.0",
		    "org.slf4j" % "slf4j-api" % "1.6.4",
		    "com.wordnik" % "swagger-core_2.10.0" % "1.2.0",
		    "com.wordnik" % "swagger-annotations_2.10.0" % "1.2.0",
		    "javax.ws.rs" % "jsr311-api" % "1.1.1"
		), 
		path = file("./linked-modules/swagger-play2")
	).settings(
	    // avoid eclipsify the subproject if aggregate is used on the parent project
		EclipseKeys.skipProject := true
	)

	lazy val play2Bragger = play.Project("play2-bragger", appVersion, 
		Seq(
			//"com.hibu" % "bragger-core" % appVersion exclude("com.wordnik", "swagger-core_2.9.1"), // comment out this line if dependsOn include braggerCoreSubProject
			//"com.wordnik" %% "swagger-play2" % "1.2.1.hibu-SNAPSHOT",                              // comment out this line if dependsOn include braggerCoreSubProject
			"org.ow2.easywsdl" % "easywsdl-tool-java2wsdl" % "2.3",
			"org.slf4j" % "slf4j-api" % "1.7.2"
		), 
		path = file("./linked-modules/play2-bragger")
	).settings(
	    // avoid eclipsify the subproject if aggregate is used on the parent project
		EclipseKeys.skipProject := true
	).dependsOn(
	    swaggerPlay2,
	    braggerCoreSubProject
	).aggregate(
	    swaggerPlay2,
	    braggerCoreSubProject
	)
	
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(

		// sbt eclipse
        EclipseKeys.skipParents in ThisBuild := false,
        EclipseKeys.withSource in ThisBuild := true,
                
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
	    play2Bragger
	).aggregate(
	    play2Bragger
	)

}
