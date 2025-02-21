package samoyed.core.lib.db.writer

import samoyed.core.model.db.SamoyedUser
import scalikejdbc.*

object SamoyedUserWriter {
  def write(samoyedUser: SamoyedUser)(using DBSession): Unit = {
    val column = SamoyedUser.column
    withSQL {
      insert
        .into(SamoyedUser)
        .namedValues(
          column.spotifyUserId -> samoyedUser.spotifyUserId,
          column.createdAt -> samoyedUser.createdAt,
          column.updatedAt -> samoyedUser.updatedAt,
          column.deletedAt -> samoyedUser.deletedAt,
          column.lockVersion -> samoyedUser.lockVersion
        )
    }.update.apply()
  }

}
