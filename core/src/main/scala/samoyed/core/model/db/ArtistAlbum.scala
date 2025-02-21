package samoyed.core.model.db

import scalikejdbc._

import java.time.OffsetDateTime

case class ArtistAlbum(
    id: Long,
    artistId: Long,
    spotifyAlbumId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object ArtistAlbum extends SQLSyntaxSupport[ArtistAlbum] {
  override val tableName = "artist_albums"
  def apply(rn: ResultName[ArtistAlbum])(rs: WrappedResultSet): ArtistAlbum = autoConstruct(rs, rn)
}
