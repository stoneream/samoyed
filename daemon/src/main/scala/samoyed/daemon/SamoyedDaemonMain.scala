package samoyed.daemon

import com.google.inject.Guice
import monix.eval.Task
import monix.execution.Scheduler.Implicits.traced
import samoyed.core.lib.config.ConfigModule
import samoyed.core.lib.db.Transaction
import samoyed.daemon.handler.create_notification.CreateNotificationHandler
import samoyed.daemon.handler.schedule_artist_album_fetch.ScheduleArtistAlbumFetchHandler
import samoyed.daemon.handler.scheduled_artist_album_detail_fetch.ScheduledArtistAlbumDetailFetchHandler
import samoyed.daemon.handler.scheduled_artist_album_fetch.ScheduledArtistAlbumFetchHandler
import samoyed.daemon.handler.send_notification.SendNotificationHandler
import samoyed.logging.Logger
import scopt.OParser

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SamoyedDaemonMain extends Logger {

  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(
      DaemonModule,
      ConfigModule
    )

    val parser = {
      val builder = OParser.builder[SamoyedDaemonCommandArgs]
      import builder.*

      OParser.sequence(
        programName("samoyed-daemon"),
        help('h', "help")
      )
    }

    OParser.parse(parser, args, SamoyedDaemonCommandArgs()) match {
      case Some(_) =>
        info("Starting Samoyed daemon")
        try {
          val tasks = Seq(
            injector.getInstance(classOf[ScheduleArtistAlbumFetchHandler]).start(),
            injector.getInstance(classOf[ScheduledArtistAlbumFetchHandler]).start(),
            injector.getInstance(classOf[ScheduledArtistAlbumDetailFetchHandler]).start(),
            injector.getInstance(classOf[CreateNotificationHandler]).start(),
            injector.getInstance(classOf[SendNotificationHandler]).start()
          )

          Await.result(Task.parSequence(tasks).runToFuture, Duration.Inf)
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
