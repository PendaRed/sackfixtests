import sbt.Keys._

// Multi project build file.  For val xxx = project, xxx is the name of the project and base dir
// logging docs: http://doc.akka.io/docs/akka/2.4.16/scala/logging.html
lazy val commonSettings = Seq(
	organization := "org.sackfix",
	version := "0.1.3",
	scalaVersion := "2.13.5",
	libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime", // without %runtime did not work in intellij
	libraryDependencies += "com.typesafe" % "config" % "1.4.1",
	libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.13",
	libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.13" % "test",
	libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.13",
	libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.6" % "test",
	libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"  % "test"
)

lazy val sffixtester = (project in file("./sf-fix-tester")).
	settings(commonSettings: _*).
	settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.6",
		name := "sf-fix-tester",
		// All tests to run one after another
		parallelExecution in test := false
	)

lazy val sackfixtests = (project in file(".")).aggregate(sffixtester)
