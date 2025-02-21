package controllers

import org.apache.pekko.actor.ActorSystem
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LoginController @Inject() (
    cc: ControllerComponents,
    errorPage: ErrorPage
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def callback(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

}
