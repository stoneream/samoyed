package samoyed.core.usecase.schedule_artist_album_fetch

import com.google.inject.{Inject, Singleton}
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.traced
import samoyed.core.usecase.schedule_artist_album_fetch.step.*

import java.time.OffsetDateTime

@Singleton
class ScheduleArtistAlbumFetch @Inject() (
    fetchArtistStep: FetchArtistStep,
    fetchScheduleStep: FetchScheduleStep,
    buildRowStep: BuildRowStep,
    writeStep: WriteStep
) {
  type Input = ScheduleArtistAlbumFetchInput
  type Output = ScheduleArtistAlbumFetchOutput
  type Exception = ScheduleArtistAlbumFetchException

  def run(input: Input): Cancelable = {
    val now = OffsetDateTime.now()

    // 同じアーティストを1日に2回以上巡回しない仕様となっている
    val task = for {
      artists <- fetchArtistStep.run()
      artistAlbumFetchSchedules <- fetchScheduleStep.run(now)
      rows <- buildRowStep.run(artistAlbumFetchSchedules, artists, now)
      _ <- writeStep.run(rows)
    } yield ScheduleArtistAlbumFetchOutput()

    task.runAsync {
      case Right(_) => ()
      case Left(e) => throw e
    }
  }
}
