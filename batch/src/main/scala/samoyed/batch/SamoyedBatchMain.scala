package samoyed.batch

import com.google.inject.Guice
import samoyed.batch.command.import_followed_artist.{ImportFollowedArtistCommandArgs, ImportFollowedArtistCommandHandler}
import samoyed.core.lib.config.ConfigModule
import samoyed.core.lib.db.Transaction
import samoyed.logging.Logger
import scopt.OParser

object SamoyedBatchMain extends Logger {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(
      ConfigModule
    )

    val parser = {
      val builder = OParser.builder[SamoyedBatchCommandArgs]
      import builder.*

      OParser.sequence(
        programName("samoyed-batch"),
        help('h', "help"),
        cmd(ImportFollowedArtistCommandHandler.name)
          .action((_, c) =>
            c.copy(
              commandName = ImportFollowedArtistCommandHandler.name,
              importFollowedArtistCommandArgs = Some(ImportFollowedArtistCommandArgs(accessToken = ""))
            )
          )
          .children(
            opt[String]('a', "access-token")
              .required()
              .action((x, c) => c.copy(importFollowedArtistCommandArgs = c.importFollowedArtistCommandArgs.map(_.copy(accessToken = x))))
              .text("Spotify access token")
          )
      )
    }

    OParser.parse(parser, args, SamoyedBatchCommandArgs()) match {
      case Some(samoyedBatchCommandArgs) =>
        try {
          samoyedBatchCommandArgs.commandName match {
            case ImportFollowedArtistCommandHandler.name =>
              val importFollowedArtistCommandHandler = injector.getInstance(classOf[ImportFollowedArtistCommandHandler])
              importFollowedArtistCommandHandler.run(samoyedBatchCommandArgs.importFollowedArtistCommandArgs.get)
            case _ =>
              error("Unknown command")
          }
        } finally {
          val tx = injector.getInstance(classOf[Transaction])
          tx.closeAll()
          info("Database connections closed")
        }
      case None =>
        error("Failed to parse command line arguments")
    }
  }
}
