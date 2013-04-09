import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

	val appName = "petstore-java-play2"
	val appVersion = "1.2.3-SNAPSHOT"

	val appDependencies: Seq[sbt.ModuleID] = Seq(
		//"com.hibu" %% "play2-bragger" % appVersion
	)

	lazy val braggerCore = Project(id = "bragger-core", 
	    base = file("./linked-modules/bragger-core"), 
	    settings = Project.defaultSettings ++ Seq(
	        libraryDependencies ++= Seq(
	            
	            // commons
        		"commons-io" % "commons-io" % "2.4",
        		"javax.mail" % "mail" % "1.4",
        		"org.slf4j" % "slf4j-api" % "1.7.2",
        		
        		// axis
        		"axis" % "axis-wsdl4j" % "1.5.1",
        		"org.apache.axis2" % "axis2" % "1.6.2",
        		"org.apache.axis2" % "axis2-java2wsdl" % "1.6.2" excludeAll(
        			ExclusionRule(organization = "org.apache.geronimo.specs"),
        			ExclusionRule(organization = "javax.servlet")
        		),
        		
        		// using patched jettison in /lib folder
        		"org.apache.axis2" % "axis2-json" % "1.6.2" exclude("org.codehaus.jettison", "jettison"),

        		// using patched jettison: 
        		// see http://www.marcusschiesser.de/2009/01/building-a-json-web-service-with-java-and-axis2/
        		// see http://markmail.org/message/cu2tw43qnrqgqgwp
        		// see http://dktiwari-hbti.blogspot.co.uk/2012/07/java-web-services.html
        		"org.codehaus.jettison" % "jettison" % "1.3.3.hibu",
        		
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
	    braggerCore
	).aggregate(
	    swaggerPlay2,
	    braggerCore
	)
	
	lazy val main = play.Project(appName, appVersion, appDependencies).settings(
                
		// bragger repo
		resolvers in ThisBuild += "github pages repo" at "http://yelllabs.github.com/bragger",
		// easywsdl
		resolvers in ThisBuild += "petalslink" at "http://maven.petalslink.com/public",
		
		// with these lines, the 'play eclipse' command adds automatically the source folders to the porject
		// remove these when getting the linked modules as managed dependencies
		// put here an entry for each element specified in the dependsOn (directly or recursively across all the subprojects)
		unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "linked-modules" / "bragger-core" / "src" / "main" / "java" ),
		unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "linked-modules" / "play2-bragger" / "app"),
		unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "linked-modules" / "swagger-play2" / "app"),
		
		// sbt eclipse
		EclipseKeys.skipParents in ThisBuild := false,
		EclipseKeys.withSource in ThisBuild := true
		
	).dependsOn(
	    play2Bragger
	).aggregate(
	    play2Bragger
	)

}
