package samoyed.core.lib.db.reader

import samoyed.core.model.db.SamoyedUser
import scalikejdbc.*

object UserReader {
  def findBySpotifyUserId(spotifyUserId: String)(using DBSession): Option[SamoyedUser] = {
    val u = SamoyedUser.syntax("user")
    withSQL {
      selectFrom(SamoyedUser as u).where
        .eq(u.spotifyUserId, spotifyUserId)
        .and
        .isNull(u.deletedAt)
    }.map(SamoyedUser(u.resultName)).single.apply()
  }

}
