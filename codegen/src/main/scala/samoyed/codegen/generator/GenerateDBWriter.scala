package samoyed.codegen.generator

import samoyed.codegen.model.GeneratedCode

object GenerateDBWriter {

  def run(
      tableName: String
  ): Unit = {
    render(tableName).foreach { GeneratedCode.write }
  }

  def render(
      tableName: String
  ): Seq[GeneratedCode] = {
    val syntax = tableName.filter(_.isUpper).map(_.toLower)

    val basePath = s"./core/src/main/scala/samoyed/core/db/writer"

    // language=scala
    val reader = s"""
       |package samoyed.core.db.writer
       |
       |import com.google.inject.{Inject, Singleton}
       |import samoyed.core.db.Transaction
       |import samoyed.core.lib.execution_context.ExecutionContextManager
       |import scalikejdbc.*
       |import samoyed.core.model.db.${tableName}
       |import scala.concurrent.{blocking, Future, ExecutionContext}
       |
       |@Singleton
       |class ${tableName}Writer @Inject()(
       |    ecm: ExecutionContextManager,
       |    tx: Transaction
       |) {
       |  private val ${syntax} = ${tableName}.syntax("${syntax}")
       |
       |  def write(): Future[Unit] = {
       |    given ExecutionContext = ecm.dbEC
       |    tx.write { implicit session =>
       |      Future {
       |        blocking {
       |          ???
       |        }
       |      }
       |    }
       |  }
       |}
       |
       |""".stripMargin

    val readerPath = s"$basePath/${tableName}Writer.scala"

    Seq(
      GeneratedCode(readerPath, reader)
    )
  }

}
