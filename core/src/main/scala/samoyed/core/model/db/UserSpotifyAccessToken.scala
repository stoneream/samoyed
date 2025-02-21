package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserSpotifyAccessToken(
    id: Int,
    userId: Int,
    accessToken: String,
    tokenType: String,
    expiresIn: Int,
    refreshToken: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserSpotifyAccessToken extends SQLSyntaxSupport[UserSpotifyAccessToken] {
  override val tableName = "user_spotify_access_tokens"

  def apply(rn: ResultName[UserSpotifyAccessToken])(rs: WrappedResultSet): UserSpotifyAccessToken = autoConstruct(rs, rn)
}
