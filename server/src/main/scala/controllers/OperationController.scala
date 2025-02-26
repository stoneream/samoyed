package controllers

import actions.SamoyedSessionAction
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.db.reader.UserFollowedArtistsImportScheduleReader
import samoyed.core.lib.db.writer.UserFollowedArtistsImportScheduleWriter
import scalikejdbc.DBSession
import views.Template

import java.time.OffsetDateTime
import javax.inject.{Inject, Singleton}

@Singleton
class OperationController @Inject() (
    cc: ControllerComponents,
    sessionAction: SamoyedSessionAction,
    transaction: Transaction,
    template: Template
) extends AbstractController(cc) {
  // フォロー中アーティストの取り込みキューイング
  def importFollowingQueue(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def postImportFollowingQueue(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    // 現在進行中の取り込みがあるか？
    val progress = transaction.read { session =>
      given DBSession = session
      UserFollowedArtistsImportScheduleReader.findProgressByUserId(sessionRequest.user.id)
    }
    if (progress.isDefined) {
      // 進行中のものが存在する場合はエラー
      ???
    } else {
      // 取り込みをキューイング
      transaction.write { session =>
        given DBSession = session
        UserFollowedArtistsImportScheduleWriter.queue(sessionRequest.user.id, OffsetDateTime.now())
      }
      ???
    }
  }

  // レーベルの通知除外の設定
  def labelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def postLabelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
}
