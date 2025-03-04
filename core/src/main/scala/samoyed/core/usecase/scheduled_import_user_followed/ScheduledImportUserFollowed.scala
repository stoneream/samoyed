package samoyed.core.usecase.scheduled_import_user_followed

import com.google.inject.{Inject, Singleton}

@Singleton
class ScheduledImportUserFollowed @Inject() (
) {
  type Input = ScheduledImportUserFollowedInput
  type Output = ScheduledImportUserFollowedOutput
  type Exception = ScheduledImportUserFollowedException

  def run(input: Input) = {
    // スケジュール中の取り込みキューを取得

    // 取り込み処理

    ???
  }
}
