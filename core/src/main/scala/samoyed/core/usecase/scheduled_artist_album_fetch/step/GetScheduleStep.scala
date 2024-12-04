package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{Artist, ArtistAlbumFetchSchedule}
import samoyed.logging.Logger
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_fetch] class GetScheduleStep @Inject() (
    tx: Transaction
) extends Logger {
  private val aafs = ArtistAlbumFetchSchedule.syntax("aafs")
  private val a = Artist.syntax("a")

  /**
   * 未開始のスケジュールをn件取得し、開始状態に遷移
   */
  def run(n: Int, now: OffsetDateTime): Task[List[(ArtistAlbumFetchSchedule, Artist)]] = {
    tx.readForWrite { implicit session =>
      // 未開始のスケジュールをn件取得
      val schedulesWithArtist = withSQL {
        select(aafs.result.*, a.result.*)
          .from(ArtistAlbumFetchSchedule as aafs)
          .join(Artist as a)
          .on(aafs.artistId, a.id)
          .where
          .isNull(aafs.startedAt)
          .and
          .isNull(aafs.finishedAt)
          .and
          .isNull(aafs.deletedAt)
          .and
          .isNull(a.deletedAt)
          .limit(n)
      }.map { rs =>
        (ArtistAlbumFetchSchedule(aafs.resultName)(rs), Artist(a.resultName)(rs))
      }.list
        .apply()

      // 取得したスケジュールを開始状態に遷移
      val updatedSchedulesWithArtist = schedulesWithArtist.map { case (schedule, artist) =>
        val updatedSchedule = schedule.copy(
          startedAt = Some(now),
          updatedAt = now
        )
        (updatedSchedule, artist)
      }

      info("Found schedules ({})", kv("count", updatedSchedulesWithArtist.size))

      // 開始状態に遷移したスケジュールを更新
      val scheduleIds = schedulesWithArtist.map { case (schedule, artist) => schedule.id }
      withSQL {
        update(ArtistAlbumFetchSchedule as aafs)
          .set(
            aafs.startedAt -> now,
            aafs.updatedAt -> now
          )
          .where
          .in(aafs.id, scheduleIds)
      }.update.apply()

      updatedSchedulesWithArtist
    }
  }
}
