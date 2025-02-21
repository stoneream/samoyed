package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbumDetailFetchSchedule(
    id: Long,
    artistAlbumId: Long,
    scheduledAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime],
    finishedAt: Option[OffsetDateTime],
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object ArtistAlbumDetailFetchSchedule extends SQLSyntaxSupport[ArtistAlbumDetailFetchSchedule] {
  override val tableName = "artist_album_detail_fetch_schedules"
  def apply(rn: ResultName[ArtistAlbumDetailFetchSchedule])(rs: WrappedResultSet): ArtistAlbumDetailFetchSchedule = autoConstruct(rs, rn)
}
