package samoyed.core.usecase.schedule_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.ArtistAlbumFetchSchedule
import scalikejdbc.*

@Singleton
private[schedule_artist_album_fetch] class WriteStep @Inject() (
    tx: Transaction
) {
  private val column = ArtistAlbumFetchSchedule.column
  
  def run(
      rows: List[ArtistAlbumFetchSchedule]
  ) = {
    tx.write { implicit session =>
      val builder = BatchParamsBuilder {
        rows.map { row =>
          Seq(
            column.artistId -> row.artistId,
            column.scheduledAt -> row.scheduledAt,
            column.startedAt -> row.startedAt,
            column.finishedAt -> row.finishedAt,
            column.createdAt -> row.createdAt,
            column.updatedAt -> row.updatedAt,
            column.deletedAt -> row.deletedAt,
            column.lockVersion -> row.lockVersion
          )
        }
      }
      withSQL {
        insert.into(ArtistAlbumFetchSchedule).namedValues(builder.columnsAndPlaceholders*)
      }.batch(builder.batchParams*).apply()
    }
  }
}
