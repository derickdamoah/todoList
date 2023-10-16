import scoverage.ScoverageKeys

name := """toDo-list"""
organization := "derickdamoah.com"

version := "1.0-SNAPSHOT"

lazy val IntegrationTest = config("it") extend Test

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .configs(IntegrationTest)
  .settings(
  assembly / mainClass := Some("Main")
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.testSettings) *)
  .settings(
    IntegrationTest / testOptions := Seq(Tests.Argument("-n", "integration")),
    IntegrationTest / parallelExecution := false,
    IntegrationTest / sources := (Test / sources).value,
    IntegrationTest / unmanagedSourceDirectories := Seq((Test / baseDirectory).value / "it")
  )

scalaVersion := "2.13.11"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.20" // Adjust the version as needed
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.10.2"
libraryDependencies += "org.mockito" %  "mockito-core"  % "5.4.0" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test
libraryDependencies += "org.jsoup" % "jsoup" % "1.16.1"
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
assembly / assemblyOutputPath := new File(System.getProperty("user.dir") + "/toDo-list-assembly-1.0-SNAPSHOT.jar")


ScoverageKeys.coverageExcludedPackages := "<empty>;views.html.*;controllers.Assets*;prod.*;.*Reverse.*;.*javascript.*;.*Routes.*;.*TwirlTemplates*;app.*.Routes"
ScoverageKeys.coverageMinimumStmtTotal := 90
ScoverageKeys.coverageFailOnMinimum := true
ScoverageKeys.coverageHighlighting := true