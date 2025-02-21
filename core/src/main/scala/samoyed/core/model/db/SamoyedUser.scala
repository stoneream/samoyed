package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class SamoyedUser(
    id: Int,
    spotifyUserId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object SamoyedUser extends SQLSyntaxSupport[SamoyedUser] {
  override val tableName = "samoyed_users"

  def apply(rn: ResultName[SamoyedUser])(rs: WrappedResultSet): SamoyedUser = autoConstruct(rs, rn)
}
