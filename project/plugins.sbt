addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
// addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.1")
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "4.3.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.2.0")

// depends on scalikejdbc-mapper-generator
libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "3.5.3"
