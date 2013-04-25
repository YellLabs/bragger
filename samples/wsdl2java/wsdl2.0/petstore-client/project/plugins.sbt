// Comment to get more information during initialization
logLevel := Level.Info

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.0")

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
resolvers ++= Seq(
	Resolver.url("HIBU SNAPSHOTS IVY2", new URL("http://uskopciaft01.yellglobal.net:8080/artifactory/s1-libs-snapshot-local"))(Resolver.ivyStylePatterns),
	"HIBU SNAPSHOTS MAVEN" at "http://uskopciaft01.yellglobal.net:8080/artifactory/s1-libs-snapshot-local",
	"github pages repo" at "http://yelllabs.github.com/bragger",
	"petalslink" at "http://maven.petalslink.com/public"
)

addSbtPlugin("com.hibu" % "bragger-sbt" % "1.3.0-SNAPSHOT")
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
