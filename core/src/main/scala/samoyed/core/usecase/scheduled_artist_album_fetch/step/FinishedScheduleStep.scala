package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.ArtistAlbumFetchSchedule
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_fetch] class FinishedScheduleStep @Inject() (tx: Transaction) {
  private val aafs = ArtistAlbumFetchSchedule.syntax("aafs")

  def run(artistAlbumFetchSchedule: ArtistAlbumFetchSchedule, now: OffsetDateTime): Task[Int] = {
    tx.write { implicit session =>
      withSQL {
        update(ArtistAlbumFetchSchedule as aafs)
          .set(
            aafs.finishedAt -> now,
            aafs.updatedAt -> now
          )
          .where
          .eq(aafs.id, artistAlbumFetchSchedule.id)
      }.update.apply()
    }
  }
}
