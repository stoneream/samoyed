package samoyed.batch.command.import_followed_artist

import com.google.inject.Inject
import monix.execution.Callback
import samoyed.core.usecase.import_followed_artist.{ImportFollowedArtist, ImportFollowedArtistInput}
import samoyed.logging.Logger
import monix.execution.Scheduler.Implicits.traced
import java.time.OffsetDateTime

class ImportFollowedArtistCommandHandler @Inject() (
    importFollowedArtist: ImportFollowedArtist
) extends Logger {
  def run(args: ImportFollowedArtistCommandArgs): Unit = {
    val now = OffsetDateTime.now()
    val input = ImportFollowedArtistInput(args.accessToken, now)

    importFollowedArtist.run(input).runAsync { Callback.empty }
  }
}

object ImportFollowedArtistCommandHandler {
  val name = "import-followed-artist"
}
