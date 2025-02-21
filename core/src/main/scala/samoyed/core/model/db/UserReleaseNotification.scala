package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserReleaseNotification(
    id: Long,
    userId: Long,
    artistAlbumId: Long,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserReleaseNotification extends SQLSyntaxSupport[UserReleaseNotification] {
  override val tableName = "user_release_notifications"

  def apply(rn: ResultName[UserReleaseNotification])(rs: WrappedResultSet): UserReleaseNotification = autoConstruct(rs, rn)
}
