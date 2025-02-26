package controllers

import actions.SamoyedSessionAction
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import views.Template

import javax.inject.{Inject, Singleton}

@Singleton
class OperationController @Inject() (
    cc: ControllerComponents,
    sessionAction: SamoyedSessionAction,
    template: Template
) extends AbstractController(cc) {
  // フォロー中アーティストの取り込みキューイング
  def importFollowingQueue(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def postImportFollowingQueue(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }

  // レーベルの通知除外の設定
  def labelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
  def postLabelMuteSettings(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    ???
  }
}
