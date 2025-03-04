package samoyed.codegen.generator

import samoyed.codegen.model.GeneratedCode

object GenerateDaemon {

  def run(
      daemonName: String
  ): Unit = {
    render(daemonName).foreach { GeneratedCode.write }
  }

  def render(
      daemonName: String
  ): Seq[GeneratedCode] = {
    import samoyed.codegen.util.CaseConverter.*

    val daemonNameSnake = toSnakeCase(daemonName)
    val daemonNameKebab = toKebabCase(daemonName)

    val basePath = s"./daemon/src/main/scala/samoyed/daemon/handler/${daemonNameSnake}"

    // language=scala
    val handler = s"""package samoyed.daemon.handler.${daemonNameSnake}
       |import com.google.inject.{Inject, Singleton}
       |import monix.eval.Task
       |import samoyed.daemon.handler.AbstractHandler
       |
       |@Singleton
       |class ${daemonName}Handler @Inject() (
       |    config: ${daemonName}Config
       |) extends AbstractHandler("${daemonName}", config) {
       |  override def execute(): Task[Unit] = Task {
       |    throw new NotImplementedError("Not implemented")
       |  }
       |}
       |
       |object ${daemonName}Handler {
       |  val name = "${daemonNameKebab}"
       |}
       |""".stripMargin

    val handlerPath = s"$basePath/${daemonName}Handler.scala"

    // language=scala
    val handlerConfig = s"""package samoyed.daemon.handler.${daemonNameSnake}
       |
       |import samoyed.daemon.handler.AbstractHandler
       |
       |final case class ${daemonName}Config(
       |    intervalSeconds: Long = 60
       |) extends AbstractHandler.AbstractHandlerConfig
       |""".stripMargin

    val handlerConfigPath = s"$basePath/${daemonName}Config.scala"

    Seq(
      GeneratedCode(handlerPath, handler),
      GeneratedCode(handlerConfigPath, handlerConfig)
    )
  }

}
