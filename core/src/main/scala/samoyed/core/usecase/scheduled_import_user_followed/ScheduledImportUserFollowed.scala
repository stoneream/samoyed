package samoyed.core.usecase.scheduled_import_user_followed

import com.google.inject.{Inject, Singleton}

@Singleton
class ScheduledImportUserFollowed @Inject() (
) {
  type Input = ScheduledImportUserFollowedInput
  type Output = ScheduledImportUserFollowedOutput
  type Exception = ScheduledImportUserFollowedException

  def run(input: Input) = {

    // 未開始の取り込みスケジュールを1件取得、開始状態に遷移させる

    // 対象ユーザーの最新のアクセストークンを取得する

    // フォロー中のアーティストを取得する

    // レコードを作成

    ???
  }
}
