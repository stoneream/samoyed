package samoyed.core.lib.db.writer

import samoyed.core.model.db.SamoyedSession
import scalikejdbc.*

object SamoyedSessionWriter {

  def write(session: SamoyedSession)(using DBSession): Unit = {
    val column = SamoyedSession.column
    withSQL {
      insert
        .into(SamoyedSession)
        .namedValues(
          column.clientId -> session.clientId,
          column.redirectUri -> session.redirectUri,
          column.responseType -> session.responseType,
          column.state -> session.state,
          column.codeVerifier -> session.codeVerifier,
          column.codeChallengeMethod -> session.codeChallengeMethod,
          column.sessionToken -> session.sessionToken,
          column.expiresIn -> session.expiresIn,
          column.createdAt -> session.createdAt,
          column.updatedAt -> session.updatedAt,
          column.deletedAt -> session.deletedAt,
          column.lockVersion -> session.lockVersion
        )
    }.update.apply()
  }

}
