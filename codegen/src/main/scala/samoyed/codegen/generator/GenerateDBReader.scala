package samoyed.codegen.generator

import samoyed.codegen.model.GeneratedCode

object GenerateDBReader {

  def run(
      tableName: String
  ): Unit = {
    render(tableName).foreach { GeneratedCode.write }
  }

  def render(
      tableName: String
  ): Seq[GeneratedCode] = {
    val syntax = tableName.filter(_.isUpper).map(_.toLower)

    val basePath = s"./core/src/main/scala/samoyed/core/db/reader"

    // language=scala
    val reader = s"""
       |package samoyed.core.db.reader
       |
       |import com.google.inject.{Inject, Singleton}
       |import samoyed.core.db.Transaction
       |import samoyed.core.lib.execution_context.ExecutionContextManager
       |import scalikejdbc.*
       |import samoyed.core.model.db.${tableName}
       |import scala.concurrent.{blocking, Future, ExecutionContext}
       |
       |@Singleton
       |class ${tableName}Reader @Inject()(
       |    ecm: ExecutionContextManager,
       |    tx: Transaction
       |) {
       |  private val ${syntax} = ${tableName}.syntax("${syntax}")
       |
       |  def find = {
       |    given ExecutionContext = ecm.dbEC
       |    tx.read { implicit session =>
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

    val readerPath = s"$basePath/${tableName}Reader.scala"

    Seq(
      GeneratedCode(readerPath, reader)
    )
  }

}
