package samoyed.core.lib.db.reader

import samoyed.core.model.db.{SamoyedUser, UserFollowedArtistsImportSchedule}
import scalikejdbc.*

object UserFollowedArtistsImportScheduleReader {

  def findProgressByUserId(userId: Long)(using DBSession): Option[UserFollowedArtistsImportSchedule] = {
    val ufas = UserFollowedArtistsImportSchedule.syntax("ufas")
    withSQL {
      selectFrom(UserFollowedArtistsImportSchedule as ufas).where
        .eq(ufas.userId, userId)
        .and
        .isNull(ufas.deletedAt)
        .and
        .isNotNull(ufas.startedAt)
        .and
        .isNull(ufas.finishedAt)
    }.map(UserFollowedArtistsImportSchedule(ufas.resultName)).single.apply()
  }

}
