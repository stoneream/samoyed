package samoyed.core.usecase.schedule_artist_album_fetch.step

import com.google.inject.Singleton
import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.model.db.{Artist, ArtistAlbumFetchSchedule}
import samoyed.logging.Logger

import java.time.OffsetDateTime

@Singleton
private[schedule_artist_album_fetch] class BuildRowStep extends Logger {
  def run(
      artistAlbumFetchSchedules: List[ArtistAlbumFetchSchedule],
      artists: List[Artist],
      now: OffsetDateTime
  ): Task[List[ArtistAlbumFetchSchedule]] = Task {
    val artistsToSchedule = excludeAlreadyScheduledArtists(artistAlbumFetchSchedules, artists)

    val excludedArtists = artists.toSet -- artistsToSchedule.toSet
    info("excluded artists ({})", kv("count", excludedArtists.size))

    artistsToSchedule.map { artist =>
      ArtistAlbumFetchSchedule(
        id = 0,
        artistId = artist.id,
        scheduledAt = now,
        startedAt = None,
        finishedAt = None,
        createdAt = now,
        updatedAt = now,
        deletedAt = None,
        lockVersion = 0
      )
    }
  }

  /**
   * 当日すでに巡回予定のアーティストを除外する
   */
  private def excludeAlreadyScheduledArtists(
      artistAlbumFetchSchedule: List[ArtistAlbumFetchSchedule],
      artists: List[Artist]
  ): List[Artist] = {
    val scheduledArtistIds = artistAlbumFetchSchedule.map(_.artistId)
    artists.filterNot(artist => scheduledArtistIds.contains(artist.id))
  }
}
