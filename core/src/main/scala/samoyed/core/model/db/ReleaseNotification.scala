package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ReleaseNotification(
    id: Int,
    artistAlbumId: Int,
    notificationSentAt: Option[OffsetDateTime],
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime]
)

object ReleaseNotification extends SQLSyntaxSupport[ReleaseNotification] {
  def apply(rn: ResultName[ReleaseNotification])(rs: WrappedResultSet): ReleaseNotification = autoConstruct(rs, rn)
}
