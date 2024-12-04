package samoyed.core.usecase.scheduled_artist_album_detail_fetch

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import monix.execution.Cancelable
import samoyed.core.usecase.scheduled_artist_album_detail_fetch.step.*
import samoyed.logging.Logger
import monix.execution.Scheduler.Implicits.traced
import net.logstash.logback.argument.StructuredArguments.kv

import java.time.OffsetDateTime

@Singleton
class ScheduledArtistAlbumDetailFetch @Inject() (
    getScheduleStep: GetScheduleStep,
    fetchArtistAlbumDetailStep: FetchArtistAlbumDetailStep,
    saveArtistAlbumDetailStep: SaveArtistAlbumDetailStep,
    finishedScheduleStep: FinishedScheduleStep
) extends Logger {
  type Input = ScheduledArtistAlbumDetailFetchInput
  type Output = ScheduledArtistAlbumDetailFetchOutput
  type Exception = ScheduledArtistAlbumDetailFetchException

  def run(input: Input): Cancelable = {
    val task = for {
      // 未実行の取得スケジュールを取得 & 開始状態に遷移
      scheduleWithArtistAlbums <- getScheduleStep.run(input.processCount, OffsetDateTime.now())
      _ <-
        if (scheduleWithArtistAlbums.isEmpty) {
          Task.unit
        } else {
          for {
            // アルバム詳細取得
            details <- fetchArtistAlbumDetailStep.run(scheduleWithArtistAlbums)
            // 取得したアルバム詳細をDBに保存
            _ <- saveArtistAlbumDetailStep.run(details, OffsetDateTime.now())
            // スケジュールを完了状態に遷移
            schedules = scheduleWithArtistAlbums.map(_._1)
            _ <- finishedScheduleStep.run(schedules, OffsetDateTime.now())
          } yield ()
        }
    } yield ()

    task.runAsync {
      case Right(_) => // do nothing
      case Left(e) => error("Failed to fetch artist album detail", e)
    }
  }
}
