package samoyed.daemon.handler.scheduled_artist_album_detail_fetch
import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.usecase.scheduled_artist_album_detail_fetch.{ScheduledArtistAlbumDetailFetch, ScheduledArtistAlbumDetailFetchInput}
import samoyed.daemon.handler.AbstractHandler

@Singleton
class ScheduledArtistAlbumDetailFetchHandler @Inject() (
    config: ScheduledArtistAlbumDetailFetchConfig,
    scheduledArtistAlbumDetailFetch: ScheduledArtistAlbumDetailFetch
) extends AbstractHandler("ScheduledArtistAlbumDetailFetch", config) {
  override def execute(): Task[Unit] = Task {
    val input = ScheduledArtistAlbumDetailFetchInput()
    scheduledArtistAlbumDetailFetch.run(input)
  }
}
