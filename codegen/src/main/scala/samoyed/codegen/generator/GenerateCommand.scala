package samoyed.codegen.generator

import samoyed.codegen.model.GeneratedCode

object GenerateCommand {

  def run(
      commandName: String
  ): Unit = {
    render(commandName).foreach { GeneratedCode.write }
  }

  def render(
      commandName: String
  ): Seq[GeneratedCode] = {
    import samoyed.codegen.util.CaseConverter.*

    val commandNameSnake = toSnakeCase(commandName)
    val commandNameKebab = toKebabCase(commandName)

    val basePath = s"./batch/src/main/scala/samoyed/batch/command/${commandNameSnake}"

    // language=scala
    val command = s"""package samoyed.batch.command.${commandNameSnake}
       |import com.google.inject.{Inject, Singleton}
       |import samoyed.logging.Logger
       |
       |@Singleton
       |class ${commandName}CommandHandler @Inject()() extends Logger {
       |  def run(args: ${commandName}CommandArgs) = throw new NotImplementedError("Not implemented")
       |}
       |
       |object ${commandName}CommandHandler {
       |  val name = "${commandNameKebab}"
       |}
       |""".stripMargin

    val commandPath = s"$basePath/${commandName}CommandHandler.scala"

    // language=scala
    val commandArgs = s"""package samoyed.batch.command.${commandNameSnake}
       |
       |final case class ${commandName}CommandArgs()
       |""".stripMargin

    val commandArgsPath = s"$basePath/${commandName}CommandArgs.scala"

    Seq(
      GeneratedCode(commandPath, command),
      GeneratedCode(commandArgsPath, commandArgs)
    )
  }

}
