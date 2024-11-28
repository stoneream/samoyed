package samoyed.core.model.db

import scalikejdbc._

import java.time.OffsetDateTime

case class Artist(
    id: Int,
    name: String,
    spotifyArtistId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object Artist extends SQLSyntaxSupport[Artist] {
  def apply(rn: ResultName[Artist])(rs: WrappedResultSet): Artist = autoConstruct(rs, rn)
}
