package samoyed.daemon.handler.schedule_artist_album_fetch

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.usecase.schedule_artist_album_fetch.{ScheduleArtistAlbumFetch, ScheduleArtistAlbumFetchInput}
import samoyed.daemon.handler.AbstractHandler

@Singleton
class ScheduleArtistAlbumFetchHandler @Inject() (
    config: ScheduleArtistAlbumFetchConfig,
    scheduleArtistAlbumFetch: ScheduleArtistAlbumFetch
) extends AbstractHandler("ScheduleArtistAlbumFetch", config) {
  override def execute(): Task[Unit] = Task {
    val input = ScheduleArtistAlbumFetchInput()
    scheduleArtistAlbumFetch.run(input)
  }
}

object ScheduleArtistAlbumFetchHandler {
  val name = "schedule-artist-album-fetch"
}
