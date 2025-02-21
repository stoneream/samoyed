package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtistsImportQueue(
    id: Int,
    userId: Int,
    queuedAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime],
    finishedAt: Option[OffsetDateTime],
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserFollowedArtistsImportQueue extends SQLSyntaxSupport[UserFollowedArtistsImportQueue] {
  override val tableName = "user_followed_artists_import_queue"

  def apply(rn: ResultName[UserFollowedArtistsImportQueue])(rs: WrappedResultSet): UserFollowedArtistsImportQueue = autoConstruct(rs, rn)
}
