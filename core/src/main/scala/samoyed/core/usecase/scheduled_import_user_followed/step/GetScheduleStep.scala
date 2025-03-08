package samoyed.core.usecase.scheduled_import_user_followed.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.UserFollowedArtistsImportSchedule
import samoyed.logging.Logger

@Singleton
private[scheduled_import_user_followed] class GetScheduleStep @Inject() (
    tx: Transaction
) extends Logger {

  def run(): Task[UserFollowedArtistsImportSchedule] = {

    // 未開始の取り込みスケジュールを1件取得、開始状態に遷移させる

    ???
  }
}
