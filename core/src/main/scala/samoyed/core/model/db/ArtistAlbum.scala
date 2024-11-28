package samoyed.core.model.db

import scalikejdbc._

import java.time.OffsetDateTime

case class ArtistAlbum(
    id: Int,
    artistId: Int,
    spotifyAlbumId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object ArtistAlbum extends SQLSyntaxSupport[ArtistAlbum] {
  def apply(rn: ResultName[ArtistAlbum])(rs: WrappedResultSet): ArtistAlbum = autoConstruct(rs, rn)
}
