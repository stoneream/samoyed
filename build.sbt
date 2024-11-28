lazy val baseSettings = Seq(
  // === project info ===
  organization := "io.github.stoneream",
  developers := List(
    Developer(
      "stoneream",
      "Ishikawa Ryuto",
      "ishikawa-r@protonmail.com",
      url("https://github.com/stoneream")
    )
  ),
  // === scala settings ===
  scalaVersion := "3.5.1",
  scalafmtOnCompile := true,
  scalacOptions ++= Seq(
    "-Yretain-trees",
    "-Wunused:all"
  ),
  semanticdbEnabled := true,
  fork := true,
  // === sbt-assembly settings ===
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs @ _*) =>
      (xs map { _.toLowerCase }) match {
        case "services" :: xs =>
          MergeStrategy.filterDistinctLines
        case _ => MergeStrategy.discard
      }
    case x if x.endsWith("module-info.class") => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
      oldStrategy(x)
  },
  assembly / assemblyJarName := s"${name.value}.jar"
)

// === project setting ===

lazy val root = (project in file("."))
  .settings(
    name := "samoyed"
  )
  .settings(baseSettings *)
  .aggregate(core, codegen, batch, daemon, logging)

lazy val core = (project in file("core"))
  .settings(
    name := "samoyed-core",
    libraryDependencies ++= Dependencies.core
  )
  .settings(baseSettings *)
  .dependsOn(logging)

lazy val codegen = (project in file("codegen"))
  .settings(
    name := "samoyed-codegen",
    libraryDependencies ++= Dependencies.codegen
  )
  .settings(baseSettings *)
  .dependsOn(logging)

lazy val batch = (project in file("batch"))
  .settings(
    name := "samoyed-batch",
    libraryDependencies ++= Dependencies.batch,
    assembly / mainClass := Some("samoyed.batch.SamoyedBatchMain")
  )
  .settings(baseSettings *)
  .dependsOn(core)
  .dependsOn(logging)

lazy val daemon = (project in file("daemon"))
  .settings(
    name := "samoyed-daemon",
    libraryDependencies ++= Dependencies.daemon,
    assembly / mainClass := Some("samoyed.daemon.SamoyedDaemonMain")
  )
  .settings(baseSettings *)
  .dependsOn(core)
  .dependsOn(logging)

lazy val logging = (project in file("logging"))
  .settings(
    name := "samoyed-logging",
    libraryDependencies ++= Dependencies.logging
  )
  .settings(baseSettings *)
