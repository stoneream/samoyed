package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtist(
    id: Long,
    userId: Long,
    artistId: Long,
    userFollowedArtistsImportQueueId: Long,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object UserFollowedArtist extends SQLSyntaxSupport[UserFollowedArtist] {
  override val tableName = "user_followed_artists"
  def apply(rn: ResultName[UserFollowedArtist])(rs: WrappedResultSet): UserFollowedArtist = autoConstruct(rs, rn)
}
