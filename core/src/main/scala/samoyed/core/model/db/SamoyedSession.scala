package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class SamoyedSession(
    id: Int,
    clientId: String,
    redirectUri: String,
    responseType: String,
    state: String,
    codeVerifier: String,
    codeChallengeMethod: String,
    sessionToken: String,
    expiresIn: Int,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object SamoyedSession extends SQLSyntaxSupport[SamoyedSession] {
  override val tableName = "samoyed_sessions"

  def apply(rn: ResultName[SamoyedSession])(rs: WrappedResultSet): SamoyedSession = autoConstruct(rs, rn)

  val EXPIRES_IN: Int = 3600 // 1 hour
}
