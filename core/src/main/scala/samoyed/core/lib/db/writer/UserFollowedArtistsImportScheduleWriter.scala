package samoyed.core.lib.db.writer

import samoyed.core.model.db.UserFollowedArtistsImportSchedule
import scalikejdbc.*

import java.time.OffsetDateTime

object UserFollowedArtistsImportScheduleWriter {
  def write(row: UserFollowedArtistsImportSchedule)(using DBSession): Unit = {
    val column = UserFollowedArtistsImportSchedule.column

    withSQL {
      insert
        .into(UserFollowedArtistsImportSchedule)
        .namedValues(
          column.userId -> row.userId,
          column.queuedAt -> row.queuedAt,
          column.startedAt -> row.startedAt,
          column.finishedAt -> row.finishedAt,
          column.createdAt -> row.createdAt,
          column.updatedAt -> row.updatedAt,
          column.deletedAt -> row.deletedAt,
          column.lockVersion -> row.lockVersion
        )
    }.update.apply()
  }

  def queue(userId: Long, now: OffsetDateTime)(using DBSession): Unit = {
    val column = UserFollowedArtistsImportSchedule.column

    withSQL {
      insert
        .into(UserFollowedArtistsImportSchedule)
        .namedValues(
          column.userId -> userId,
          column.queuedAt -> now,
          column.createdAt -> now,
          column.updatedAt -> now,
          column.lockVersion -> 0
        )
    }.update.apply()
  }

}
