package samoyed.core.model.db

import scalikejdbc.*

import java.time.{LocalDate, OffsetDateTime}

case class ArtistAlbumDetail(
    id: Int,
    artistAlbumId: Int,
    albumName: String,
    releaseDate: LocalDate,
    releaseDateType: String,
    label: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object ArtistAlbumDetail extends SQLSyntaxSupport[ArtistAlbumDetail] {
  override val tableName = "artist_album_details"
  def apply(rn: ResultName[ArtistAlbumDetail])(rs: WrappedResultSet): ArtistAlbumDetail = autoConstruct(rs, rn)
}
