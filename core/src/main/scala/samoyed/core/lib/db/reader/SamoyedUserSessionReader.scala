package samoyed.core.lib.db.reader

import samoyed.core.model.db.{SamoyedSession, SamoyedUser, SamoyedUserSession}
import scalikejdbc.*

object SamoyedUserSessionReader {
  def findBySessionToken(sessionToken: String)(using DBSession): Option[(SamoyedUserSession, SamoyedUser)] = {
    val sus = SamoyedUserSession.syntax("sus")
    val su = SamoyedUser.syntax("su")
    withSQL {
      selectFrom(SamoyedUserSession as sus)
        .join(SamoyedUser as su)
        .on(
          sqls
            .eq(sus.userId, su.id)
            .and
            .isNull(su.deletedAt)
        )
        .where
        .eq(sus.sessionToken, sessionToken)
        .and
        .isNull(sus.deletedAt)
    }.map { rs =>
      val userSession = SamoyedUserSession(sus.resultName)(rs)
      val user = SamoyedUser(su.resultName)(rs)
      (userSession, user)
    }.single
      .apply()
  }
}
