package samoyed.core.usecase.schedule_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.ArtistAlbumFetchSchedule
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
private[schedule_artist_album_fetch] class FetchScheduleStep @Inject() (
    tx: Transaction
) {
  private val aafs = ArtistAlbumFetchSchedule.syntax("aafs")

  /**
   * 当日作成済みのスケジュールを取得
   * @param now
   * @return
   */
  def run(now: OffsetDateTime): Task[List[ArtistAlbumFetchSchedule]] = {
    val start = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
    val end = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999)

    tx.read { implicit session =>
      withSQL {
        select(aafs.result.*)
          .from(ArtistAlbumFetchSchedule as aafs)
          .where
          .between(aafs.scheduledAt, start, end)
          .and
          .isNull(aafs.deletedAt)
      }.map(ArtistAlbumFetchSchedule(aafs.resultName)).list.apply()
    }
  }
}
