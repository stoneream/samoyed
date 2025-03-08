package samoyed.core.lib.db.reader

import samoyed.core.model.db.UserFollowedArtistsImportSchedule
import scalikejdbc.*

object UserFollowedArtistsImportScheduleReader {

  def findProgressOrQueueByUserId(samoyedUserId: Long)(using DBSession): List[UserFollowedArtistsImportSchedule] = {
    val ufas = UserFollowedArtistsImportSchedule.syntax("ufas")
    withSQL {
      selectFrom(UserFollowedArtistsImportSchedule as ufas).where
        .eq(ufas.samoyedUserId, samoyedUserId)
        .and
        .isNull(ufas.deletedAt)
        .and
        .isNull(ufas.finishedAt)
    }.map(UserFollowedArtistsImportSchedule(ufas.resultName)).list.apply()
  }

}
