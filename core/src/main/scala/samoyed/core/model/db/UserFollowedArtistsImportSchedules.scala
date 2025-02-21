package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtistsImportSchedules(
    id: Long,
    userId: Long,
    queuedAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime],
    finishedAt: Option[OffsetDateTime],
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserFollowedArtistsImportSchedules extends SQLSyntaxSupport[UserFollowedArtistsImportSchedules] {
  override val tableName = "user_followed_artists_import_schedules"

  def apply(rn: ResultName[UserFollowedArtistsImportSchedules])(rs: WrappedResultSet): UserFollowedArtistsImportSchedules = autoConstruct(rs, rn)
}
