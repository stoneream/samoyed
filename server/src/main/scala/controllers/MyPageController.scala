package controllers

import actions.SamoyedSessionAction
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import views.Template

import javax.inject.{Inject, Singleton}

@Singleton
class MyPageController @Inject() (
    cc: ControllerComponents,
    sessionAction: SamoyedSessionAction,
    template: Template
) extends AbstractController(cc) {

  def index(): Action[AnyContent] = sessionAction.samoyedUserSession { sessionRequest =>
    val userId = sessionRequest.user.spotifyUserId
    Ok(
      template.render(
        "My Page",
        views.mypage.Index.template(
          views.mypage.Index.Props(
            spotifyUserId = userId
          )
        )
      )
    )
  }
}
