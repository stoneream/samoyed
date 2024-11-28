package samoyed.daemon.handler.scheduled_artist_album_fetch
import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.usecase.scheduled_artist_album_fetch.{ScheduledArtistAlbumFetch, ScheduledArtistAlbumFetchInput}
import samoyed.daemon.handler.AbstractHandler

@Singleton
class ScheduledArtistAlbumFetchHandler @Inject() (
    config: ScheduledArtistAlbumFetchConfig,
    scheduledArtistAlbumFetch: ScheduledArtistAlbumFetch
) extends AbstractHandler("ScheduledArtistAlbumFetch", config) {
  override def execute(): Task[Unit] = Task {
    val input = ScheduledArtistAlbumFetchInput(100)
    scheduledArtistAlbumFetch.run(input)
  }
}
