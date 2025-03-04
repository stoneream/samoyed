package samoyed.core.lib.db.reader

import samoyed.core.model.db.SamoyedSession
import scalikejdbc.*

object SamoyedSessionReader {
  def findBySessionToken(sessionToken: String)(using DBSession): Option[SamoyedSession] = {
    val ss = SamoyedSession.syntax("ss")
    withSQL {
      selectFrom(SamoyedSession as ss).where
        .eq(ss.sessionToken, sessionToken)
        .and
        .isNull(ss.deletedAt)
    }.map(SamoyedSession(ss.resultName)).single.apply()
  }
}
