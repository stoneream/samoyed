package samoyed.core.lib.db.writer

import samoyed.core.model.db.UserSpotifyAccessToken
import scalikejdbc.*

object UserSpotifyAccessTokenWriter {

  def write(userSpotifyAccessToken: UserSpotifyAccessToken)(using DBSession): Unit = {
    val column = UserSpotifyAccessToken.column
    withSQL {
      insert
        .into(UserSpotifyAccessToken)
        .namedValues(
          column.userId -> userSpotifyAccessToken.userId,
          column.accessToken -> userSpotifyAccessToken.accessToken,
          column.tokenType -> userSpotifyAccessToken.tokenType,
          column.expiresIn -> userSpotifyAccessToken.expiresIn,
          column.refreshToken -> userSpotifyAccessToken.refreshToken,
          column.createdAt -> userSpotifyAccessToken.createdAt,
          column.updatedAt -> userSpotifyAccessToken.updatedAt,
          column.deletedAt -> userSpotifyAccessToken.deletedAt,
          column.lockVersion -> userSpotifyAccessToken.lockVersion
        )
    }.update.apply()
  }

}
