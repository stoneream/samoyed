package samoyed.core.usecase.scheduled_artist_album_detail_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.ArtistAlbumDetailFetchSchedule
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_detail_fetch] class FinishedScheduleStep @Inject() (tx: Transaction) {
  private val aadfs = ArtistAlbumDetailFetchSchedule.syntax("aadfs")

  def run(artistAlbumFetchSchedules: List[ArtistAlbumDetailFetchSchedule], now: OffsetDateTime): Task[Int] = {
    tx.write { implicit session =>
      withSQL {
        update(ArtistAlbumDetailFetchSchedule as aadfs)
          .set(
            aadfs.finishedAt -> now,
            aadfs.updatedAt -> now
          )
          .where
          .in(aadfs.id, artistAlbumFetchSchedules.map(_.id))
      }.update.apply()
    }
  }
}
