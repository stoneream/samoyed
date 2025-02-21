package samoyed.core.lib.db.writer

import samoyed.core.model.db.{SamoyedSession, SamoyedUserSession}
import scalikejdbc.*

object SamoyedUserSessionWriter {

  def write(samoyedUserSession: SamoyedUserSession)(using DBSession): Unit = {
    val column = SamoyedUserSession.column
    withSQL {
      insert
        .into(SamoyedSession)
        .namedValues(
          column.userId -> samoyedUserSession.userId,
          column.sessionToken -> samoyedUserSession.sessionToken,
          column.expiresIn -> samoyedUserSession.expiresIn,
          column.createdAt -> samoyedUserSession.createdAt,
          column.updatedAt -> samoyedUserSession.updatedAt,
          column.deletedAt -> samoyedUserSession.deletedAt,
          column.lockVersion -> samoyedUserSession.lockVersion
        )
    }.update.apply()
  }

}
