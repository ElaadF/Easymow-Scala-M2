name := "Easymow-Scala-M2"

version := "1.0"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"

javaOptions += "-Dlog4j.configurationFile=src/main/resources/log4j2.xml"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.1"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.5.0"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.23.4" % Test

coverageExcludedPackages := "fr\\.upem\\.easymow\\.logger\\.*"
coverageExcludedPackages := "fr\\.upem\\.easymow\\.Easymow"
