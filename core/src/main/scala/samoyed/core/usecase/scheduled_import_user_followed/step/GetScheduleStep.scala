package samoyed.core.usecase.scheduled_import_user_followed.step

import monix.eval.Task
import samoyed.core.lib.db.TransactionTask
import samoyed.core.model.db.UserFollowedArtistsImportSchedule
import samoyed.logging.Logger

import javax.inject.{Inject, Singleton}

@Singleton
private[scheduled_import_user_followed] class GetScheduleStep @Inject() (
    tx: TransactionTask
) extends Logger {

  def run(): Task[UserFollowedArtistsImportSchedule] = {

    // 未開始の取り込みスケジュールを1件取得、開始状態に遷移させる
    
    ???
  }
}
