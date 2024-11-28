package samoyed.core.usecase.scheduled_artist_album_fetch

import com.google.inject.{Inject, Singleton}
import monix.execution.Cancelable
import samoyed.core.usecase.scheduled_artist_album_fetch.step.*
import monix.execution.Scheduler.Implicits.traced
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.logging.Logger

import java.time.OffsetDateTime

@Singleton
class ScheduledArtistAlbumFetch @Inject() (
    getScheduleStep: GetScheduleStep,
    fetchArtistAlbumStep: FetchArtistAlbumStep,
    filterNewAlbumsStep: FilterNewAlbumsStep,
    saveArtistAlbumStep: SaveArtistAlbumStep,
    createScheduleArtistAlbumDetailFetch: CreateScheduleArtistAlbumDetailFetch,
    finishedScheduleStep: FinishedScheduleStep
) extends Logger {
  type Input = ScheduledArtistAlbumFetchInput
  type Output = ScheduledArtistAlbumFetchOutput
  type Exception = ScheduledArtistAlbumFetchException

  def run(input: Input): Cancelable = {
    val startedAt = OffsetDateTime.now()
    // 未実行の取得スケジュールを取得 & 開始状態に遷移
    getScheduleStep.run(input.processCount, startedAt).runAsync {
      case Left(e) => error("Failed to get schedules", e)
      case Right(ls) =>
        val tasks = ls.map { case (schedule, artist) =>
          for {
            // アーティストのリリース情報を取得
            artistWithAlbums <- fetchArtistAlbumStep.run(artist)
            // 新規リリースのみに絞り込む
            artistWithNewAlbums <- filterNewAlbumsStep.run(artistWithAlbums)
            _ = {
              info(
                s"Found new albums ({},{})",
                kv("artist", artistWithNewAlbums._1.name),
                kv("new_albums", artistWithNewAlbums._2.size)
              )
            }
            fetchedAt = OffsetDateTime.now()
            // 取得したリリース情報を保存
            _ <- saveArtistAlbumStep.run(artistWithNewAlbums, fetchedAt)
            // スケジュールを完了状態に遷移
            _ <- finishedScheduleStep.run(schedule, fetchedAt)
            now = OffsetDateTime.now()
            // アルバムの詳細情報取得スケジュールを作成
            _ <- createScheduleArtistAlbumDetailFetch.run(artistWithNewAlbums, now)
          } yield ()
        }
        tasks.foreach(_.runAsync {
          case Left(e) => error("Failed to fetch artist albums", e)
          case Right(_) => // do nothing
        })
    }
  }
}
