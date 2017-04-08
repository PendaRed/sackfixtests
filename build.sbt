import sbt.Keys._

// Multi project build file.  For val xxx = project, xxx is the name of the project and base dir
// logging docs: http://doc.akka.io/docs/akka/2.4.16/scala/logging.html
lazy val commonSettings = Seq(
	organization := "org.sackfix",
	version := "0.1.0",
	scalaVersion := "2.11.7",
	libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime", // without %runtime did not work in intellij
	libraryDependencies += "com.typesafe" % "config" % "1.3.0",
	libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.16",
	libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test",
	libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",
	libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
	libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"  % "test"
)

lazy val sffixtester = (project in file("./sf-fix-tester")).
	settings(commonSettings: _*).
	settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1",
		name := "sf-fix-tester",
		// All tests to run one after another
		parallelExecution in test := false
	)

lazy val sackfixtests = (project in file(".")).aggregate(sffixtester)
