import sbt.*

object Dependencies {

  lazy val core: Seq[ModuleID] = Seq(
    scalikejdbc,
    mariadbJavaClient,
    sttp,
    hikariCP,
    spotify,
    discordWebhooks,
    scalatest,
    typesafeConfig,
    pureConfig,
    guice,
    monix
  ).flatten

  lazy val codegen: Seq[ModuleID] = Seq(
    scopt,
    betterFiles
  ).flatten

  lazy val daemon: Seq[ModuleID] = Seq(
    scopt,
    mariadbJavaClient,
    guice,
    monix,
    typesafeConfig,
    pureConfig
  ).flatten

  lazy val server: Seq[ModuleID] = Seq(
    spotify,
    scalatags
  ).flatten

  lazy val scalatags: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "scalatags" % "0.13.1"
  )

  lazy val logging: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % "1.5.8",
    "net.logstash.logback" % "logstash-logback-encoder" % "8.0"
  )

  lazy val monix: Seq[ModuleID] = Seq(
    "io.monix" %% "monix" % "3.4.1"
  )

  lazy val scalikejdbc: Seq[ModuleID] = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
    "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "4.3.2"
  )

  lazy val mariadbJavaClient: Seq[ModuleID] = Seq(
    "org.mariadb.jdbc" % "mariadb-java-client" % "3.4.1" excludeAll ExclusionRule("org.slf4j", "jcl-over-slf4j") // 依存がぶつかるので除外
  )

  lazy val hikariCP: Seq[ModuleID] = Seq(
    "com.zaxxer" % "HikariCP" % "6.0.0"
  )

  lazy val spotify: Seq[ModuleID] = Seq(
    "se.michaelthelin.spotify" % "spotify-web-api-java" % "9.1.1"
  )

  lazy val discordWebhooks: Seq[ModuleID] = Seq(
    "club.minnced" % "discord-webhooks" % "0.8.4" excludeAll ExclusionRule("org.slf4j", "jcl-over-slf4j") // 依存がぶつかるので除外
  )

  lazy val typesafeConfig: Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % "1.4.3"
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.8",
    "com.github.pureconfig" %% "pureconfig-generic-scala3" % "0.17.8"
  )

  lazy val scopt: Seq[ModuleID] = Seq(
    "com.github.scopt" %% "scopt" % "4.1.0"
  )

  lazy val betterFiles: Seq[ModuleID] = Seq(
    "com.github.pathikrit" %% "better-files" % "3.9.2"
  )

  lazy val guice: Seq[ModuleID] = Seq(
    "com.google.inject" % "guice" % "7.0.0"
  )

  lazy val sttp: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client3" %% "core" % "3.10.3",
    "com.softwaremill.sttp.client3" %% "circe" % "3.10.3",
    "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.10.3",
    "com.squareup.okhttp3" % "okhttp" % "4.12.0"
  )

  lazy val play: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
  )

  // testing
  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.18" % Test
  )
}
