package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtistsImportSchedule(
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

object UserFollowedArtistsImportSchedule extends SQLSyntaxSupport[UserFollowedArtistsImportSchedule] {
  override val tableName = "user_followed_artists_import_schedules"

  def apply(rn: ResultName[UserFollowedArtistsImportSchedule])(rs: WrappedResultSet): UserFollowedArtistsImportSchedule = autoConstruct(rs, rn)
}
