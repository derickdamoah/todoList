name := """toDo list"""
organization := "derickdamoah.com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  assembly / mainClass := Some("Main")
)

scalaVersion := "2.13.11"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.20" // Adjust the version as needed
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.10.2"
libraryDependencies += "org.mockito" %  "mockito-core"  % "5.4.0" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test
libraryDependencies += "org.jsoup" % "jsoup" % "1.16.1"
libraryDependencies += "io.cucumber" %% "cucumber-scala" % "8.17.0" % Test
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "4.13.0"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-chrome-driver" % "4.13.0"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

Test / fork := true
Test / scalacOptions += s"-Dconfig.resource=test.conf"

ThisBuild / assemblyMergeStrategy :=  {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "play/reference-overrides.conf" => MergeStrategy.first
  case "module-info.class" => MergeStrategy.first
  case other => (assembly / assemblyMergeStrategy).value(other)
}