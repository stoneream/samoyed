package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbumFetchSchedule(
    id: Int,
    artistId: Int,
    scheduledAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime],
    finishedAt: Option[OffsetDateTime],
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object ArtistAlbumFetchSchedule extends SQLSyntaxSupport[ArtistAlbumFetchSchedule] {
  def apply(rn: ResultName[ArtistAlbumFetchSchedule])(rs: WrappedResultSet): ArtistAlbumFetchSchedule = autoConstruct(rs, rn)
}
