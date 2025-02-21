package samoyed.core.model.db

import scalikejdbc._

import java.time.OffsetDateTime

case class Artist(
    id: Long,
    name: String,
    spotifyArtistId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object Artist extends SQLSyntaxSupport[Artist] {
  override val tableName = "artists"
  def apply(rn: ResultName[Artist])(rs: WrappedResultSet): Artist = autoConstruct(rs, rn)
}
