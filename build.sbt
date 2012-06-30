name := "ScrabbleSolver"

version := "1.0"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

libraryDependencies ++= Seq("org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT",
                          "com.yammer.metrics" %% "metrics-scala" % "2.1.2")
