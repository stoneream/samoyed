package controllers

import org.apache.pekko.actor.ActorSystem
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import utils.security.TokenGenerator
import utils.security.TokenGenerator.makeCodeChallenge

import java.security.{MessageDigest, SecureRandom}
import java.time.OffsetDateTime
import java.util.Base64
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.Random

@Singleton
class RegistrationController @Inject() (
    cc: ControllerComponents,
    errorPage: ErrorPage
) extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val now = OffsetDateTime.now()
    // TODO クライアントIDとリダイレクトURIを設定ファイルから取得
    val clientId = ???
    val redirectUri = ???
    val state = TokenGenerator.generate(s"${now.toEpochSecond}")
    val codeChallengeSet = makeCodeChallenge()

    // TODO 登録セッショントークンを発行

    // 認可リクエストURLを生成
    // https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow
    val authorizationEndpoint = "https://accounts.spotify.com/authorize"
    val queryParams = Map(
      "client_id" -> Seq(clientId),
      "response_type" -> Seq("code"),
      "redirect_uri" -> Seq(redirectUri),
      "state" -> Seq(state),
      "code_challenge" -> Seq(codeChallengeSet.codeChallenge),
      "code_challenge_method" -> Seq("S256")
    )

    // TODO 登録セッショントークンをCookieに保存
    Redirect(authorizationEndpoint, queryParams).withSession(("session_token", "TODO"))
  }

  def callback(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val params = for {
      sessionToken <- request.session.get("session_token")
      code <- request.queryString.get("code").flatMap(_.headOption)
      state <- request.queryString.get("state").flatMap(_.headOption)
    } yield (sessionToken, code, state)

    params.fold {
      // TODO 必要なパラメータがない
      ???
    } { case (sessionToken, code, state) =>
      // TODO アカウント作成処理
      ???
    }
  }
}
