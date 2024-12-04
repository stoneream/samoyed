package samoyed.core.usecase.scheduled_artist_album_detail_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{ArtistAlbum, ArtistAlbumDetailFetchSchedule}
import samoyed.logging.Logger
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_detail_fetch] class GetScheduleStep @Inject() (
    tx: Transaction
) extends Logger {
  private val aa = ArtistAlbum.syntax("aad")
  private val aadfs = ArtistAlbumDetailFetchSchedule.syntax("aadfs")

  /**
   * 未開始のスケジュールをn件取得し、開始状態に遷移
   */
  def run(n: Int, now: OffsetDateTime): Task[List[(ArtistAlbumDetailFetchSchedule, ArtistAlbum)]] = {
    tx.readForWrite { implicit session =>

      // 未開始のスケジュールをn件取得
      val scheduleWithArtistAlbum = withSQL {
        select(aadfs.result.*, aa.result.*)
          .from(ArtistAlbumDetailFetchSchedule as aadfs)
          .join(ArtistAlbum as aa)
          .on(aadfs.artistAlbumId, aa.id)
          .where
          .isNull(aadfs.startedAt)
          .and
          .isNull(aadfs.finishedAt)
          .and
          .isNull(aadfs.deletedAt)
          .and
          .isNull(aa.deletedAt)
          .orderBy(aadfs.createdAt)
          .limit(n)
      }.map { rs =>
        (ArtistAlbumDetailFetchSchedule(aadfs.resultName)(rs), ArtistAlbum(aa.resultName)(rs))
      }.list
        .apply()

      // 取得したスケジュールを開始状態に遷移
      val updatedScheduleWithArtistAlbum = scheduleWithArtistAlbum.map { case (schedule, artistAlbum) =>
        val updatedSchedule = schedule.copy(
          startedAt = Some(now),
          updatedAt = now
        )
        (updatedSchedule, artistAlbum)
      }

      info("Found schedules ({})", kv("count", updatedScheduleWithArtistAlbum.size))

      // 開始状態に遷移したスケジュールを更新
      withSQL {
        update(ArtistAlbumDetailFetchSchedule as aadfs)
          .set(
            aadfs.startedAt -> now,
            aadfs.updatedAt -> now
          )
          .where
          .in(aadfs.id, updatedScheduleWithArtistAlbum.map(_._1.id))
      }.update.apply()

      updatedScheduleWithArtistAlbum
    }
  }
}
