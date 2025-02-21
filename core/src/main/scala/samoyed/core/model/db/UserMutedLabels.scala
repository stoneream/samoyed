package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserMutedLabels(
    id: Int,
    userId: Int,
    labelName: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserMutedLabels extends SQLSyntaxSupport[UserMutedLabels] {
  override val tableName = "user_muted_labels"
  def apply(rn: ResultName[UserMutedLabels])(rs: WrappedResultSet): UserMutedLabels = autoConstruct(rs, rn)
}
