package controllers

import actions.SamoyedSessionAction
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.db.reader.UserFollowedArtistsImportScheduleReader
import samoyed.core.lib.db.writer.UserFollowedArtistsImportScheduleWriter
import scalikejdbc.DBSession
import view_utils.Messages
import view_utils.Messages.MessageType
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
    val messages = sessionRequest.session.get(Messages.SESSION_KEY).flatMap(Messages.fromJson(_).toOption)

    // 現在進行中の取り込みがあるか？
    val progress = transaction.read { session =>
      given DBSession = session
      UserFollowedArtistsImportScheduleReader.findProgressByUserId(sessionRequest.user.id)
    }
    val hasProgress = progress.isDefined



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
      val messages = Messages().add(MessageType.ERROR, "現在進行中の取り込み処理が存在します。")
      Redirect("/operation/import-following-queue")
        .withSession(
          Messages.SESSION_KEY -> messages.toJson
        )
    } else {
      // 取り込みをキューイング
      transaction.write { session =>
        given DBSession = session
        UserFollowedArtistsImportScheduleWriter.queue(sessionRequest.user.id, OffsetDateTime.now())
      }
      val messages = Messages().add(MessageType.INFO, "取り込みをキューイングしました。")
      Redirect("/operation/import-following-queue")
        .withSession(
          Messages.SESSION_KEY -> messages.toJson
        )
    }
  }

  // レーベルの通知除外の設定
  def labelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def postLabelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def exportLabelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def importLabelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
}
