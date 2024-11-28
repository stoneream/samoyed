package samoyed.codegen

import samoyed.codegen.generator.{GenerateCommand, GenerateDaemon, GenerateUseCase}
import samoyed.logging.Logger
import scopt.OParser

object SamoyedCodeGenMain extends Logger {
  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[CommandArgs]

    val parser = {
      import builder.*
      OParser.sequence(
        programName("SamoyedCodeGenMain"),
        head("SamoyedCodeGenMain"),
        opt[String]("usecase-name")
          .action((x, c) => c.copy(useCaseName = Some(x)))
          .text("usecase name (e.g. CrawlNewReleases)"),
        opt[String]("command-name")
          .action((x, c) => c.copy(commandName = Some(x)))
          .text("command name (e.g. FetchNewReleases)"),
        opt[String]("daemon-name")
          .action((x, c) => c.copy(daemonName = Some(x)))
          .text("daemon name (e.g. SyncUserFollowed)")
      )
    }

    OParser.parse(parser, args, CommandArgs()) match {
      case Some(config) =>
        info(s"$config")

        config.commandName match {
          case Some(commandName) => GenerateCommand.run(commandName)
          case None => // do nothing
        }

        config.useCaseName match {
          case Some(useCaseName) => GenerateUseCase.run(useCaseName)
          case None => // do nothing
        }

        config.daemonName match {
          case Some(daemonName) => GenerateDaemon.run(daemonName)
          case None => // do nothing
        }

      case _ =>
        error("Failed to parse command line arguments")
    }
  }

}
