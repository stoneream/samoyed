package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class SamoyedUserSession(
    id: Int,
    userId: Int,
    sessionToken: String,
    expiresIn: Int,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object SamoyedUserSession extends SQLSyntaxSupport[SamoyedUserSession] {
  override val tableName = "samoyed_user_sessions"

  def apply(rn: ResultName[SamoyedUserSession])(rs: WrappedResultSet): SamoyedUserSession = autoConstruct(rs, rn)

  val EXPIRES_IN: Int = 604800 // 1 week
}
