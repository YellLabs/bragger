import scala.Console._

publishTo in ThisBuild <<= (version, name)  { (v: String, name: String) => {
		val baseRepoUrl = "http://uskopciaft01.yellglobal.net:8080/artifactory/"
		// NOTE added [publish] because the repo name must be different from the one used in the resolvers+= line to download from the same repo
		if (v.trim.endsWith("SNAPSHOT")) 
			Some("Civitas Team1 SNAPSHOT MavenRepo [publish] " at baseRepoUrl + "s1-libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime())
		else
 			println(
 	YELLOW + """
 	WARNING!
 	You won't be able to publish the current project %s version %s to the internal repository %s
 	The internal repository is only used for snapshots. 
 	To publish release versions use github repository (add files to the "gh-pages" branch of this project and push to github).
 	To remove this warning, remove the build.sbt file or change the project version to a SNAPSHOT one
 	""".format(CYAN + name + YELLOW, CYAN + v + YELLOW, CYAN + baseRepoUrl + YELLOW) + 
 	RESET
 			)
			None
	}
}

// restart sbt to pick up changes to the file content
credentials += Credentials(file(".") / "publish_credentials")

// hibu private repos
resolvers in ThisBuild += "Hibu Internal SNAPSHOT MavenRepo" at "http://uskopciaft01.yellglobal.net:8080/artifactory/s1-libs-snapshot-local"

// commented out as for now the hibu repository is only used for snapshots
//resolvers in ThisBuild += "Hibu Internal RELEASE  MavenRepo" at "http://uskopciaft01.yellglobal.net:8080/artifactory/s1-libs-release-local"
