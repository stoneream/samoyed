package controllers

import actions.{SamoyedSessionAction, SamoyedSessionCookieAccessor}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import samoyed.core.lib.db.reader.UserReader
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.db.writer.{SamoyedSessionWriter, SamoyedUserSessionWriter, SamoyedUserWriter, UserSpotifyAccessTokenWriter}
import samoyed.core.lib.oauth.SpotifyOAuth
import samoyed.core.model.config.SpotifyConfig
import samoyed.core.model.db.{SamoyedSession, SamoyedUser, SamoyedUserSession, UserSpotifyAccessToken}
import samoyed.logging.Logger
import scalikejdbc.DBSession
import se.michaelthelin.spotify.SpotifyApi
import utils.security.TokenGenerator
import utils.security.TokenGenerator.makeCodeChallenge

import java.time.OffsetDateTime
import com.google.inject.{Inject, Singleton}

@Singleton
class LoginController @Inject() (
    cc: ControllerComponents,
    errorPage: ErrorPage,
    spotifyConfig: SpotifyConfig,
    transaction: Transaction,
    sessionAction: SamoyedSessionAction,
    sessionCookieAccessor: SamoyedSessionCookieAccessor,
    spotifyOAuth: SpotifyOAuth
) extends AbstractController(cc)
    with Logger {

  def index(): Action[AnyContent] = Action { request =>

    val now = OffsetDateTime.now()
    val clientId = spotifyConfig.clientId
    val responseType = "code"
    val redirectUri = spotifyConfig.redirectUri.login
    val state = TokenGenerator.generate(s"${now.toEpochSecond}")
    val codeChallengeSet = makeCodeChallenge()
    val codeChallengeMethod = "S256"

    // セッションを発行
    val registrationSession = SamoyedSession(
      id = 0,
      clientId = clientId,
      redirectUri = redirectUri,
      responseType = responseType,
      state = state,
      codeVerifier = codeChallengeSet.codeVerifier,
      codeChallengeMethod = codeChallengeMethod,
      sessionToken = TokenGenerator.generate(s"${state}+${codeChallengeSet.codeChallenge}+${now.toEpochSecond}"),
      expiresIn = SamoyedSession.EXPIRES_IN,
      createdAt = now,
      updatedAt = now,
      deletedAt = None,
      lockVersion = 0
    )
    transaction.write { session =>
      given DBSession = session
      SamoyedSessionWriter.write(registrationSession)
    }

    // 認可リクエストURLを生成
    // https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow
    val authorizationEndpoint = "https://accounts.spotify.com/authorize"
    val queryParams = Map(
      "client_id" -> Seq(clientId),
      "response_type" -> Seq(responseType),
      "redirect_uri" -> Seq(redirectUri),
      "state" -> Seq(state),
      "scope" -> Seq("user-read-private"),
      "code_challenge" -> Seq(codeChallengeSet.codeChallenge),
      "code_challenge_method" -> Seq(codeChallengeMethod)
    )

    // セッショントークンをCookieに保存
    sessionCookieAccessor.put(registrationSession.sessionToken)(Redirect(authorizationEndpoint, queryParams))
  }

  def callback(): Action[AnyContent] = sessionAction.samoyedSession { sessionRequest =>
    val params = for {
      code <- sessionRequest.queryString.get("code").flatMap(_.headOption)
      state <- sessionRequest.queryString.get("state").flatMap(_.headOption)
    } yield (code, state)

    params.fold {
      logger.info("認可レスポンスのパラメーターが不足しています。")
      errorPage.badRequest(sessionRequest.request)
    } { case (code, state) =>
      if (state != sessionRequest.samoyedSession.state) {
        logger.info("認可レスポンスのstateが一致しませんでした。({}, {})", kv("request_state", state), kv("expect_state", sessionRequest.samoyedSession.state))
        errorPage.badRequest(sessionRequest.request)
      } else {
        spotifyOAuth.accessTokenRequest(
          redirectUri = sessionRequest.samoyedSession.redirectUri,
          authCode = code,
          codeVerifier = sessionRequest.samoyedSession.codeVerifier
        ) match {
          case Left(e) =>
            logger.warn("アクセストークンリクエストに失敗しました。", e)
            errorPage.internalServerError(sessionRequest.request)
          case Right(accessTokenResponse) =>
            val now = OffsetDateTime.now()

            // Spotifyユーザー取得
            val spotifyApi = new SpotifyApi.Builder()
              .setClientId(spotifyConfig.clientId)
              .setClientSecret(spotifyConfig.clientSecret)
              .setAccessToken(accessTokenResponse.accessToken)
              .build()
            val spotifyUser = spotifyApi.getCurrentUsersProfile.build().execute()
            val userOpt = transaction
              .read { session =>
                given DBSession = session
                UserReader.findBySpotifyUserId(
                  spotifyUserId = spotifyUser.getId
                )
              }

            val user = userOpt match {
              case Some(user) =>
                logger.info("ユーザーがすでに存在します。ログインを行います。({})", kv("user_id", user.id))
                user
              case None =>
                transaction.write { session =>
                  given DBSession = session
                  SamoyedUserWriter.write(
                    SamoyedUser(
                      id = 0,
                      spotifyUserId = spotifyUser.getId,
                      createdAt = now,
                      updatedAt = now,
                      deletedAt = None,
                      lockVersion = 0
                    )
                  )
                }
                val user = transaction.read { session =>
                  given DBSession = session
                  UserReader
                    .findBySpotifyUserId(
                      spotifyUserId = spotifyUser.getId
                    )
                    .getOrElse(
                      // このケースには到達しないはず (本当はエラーページを表示するべきだが一旦...)
                      throw new IllegalStateException("ユーザーの新規登録に失敗しました。")
                    )
                }
                logger.info("ユーザーを新規登録しました。({})", kv("user_id", user.id))
                user
            }
            // アクセストークンを保管
            transaction.write { session =>
              given DBSession = session

              UserSpotifyAccessTokenWriter.write(
                UserSpotifyAccessToken(
                  id = 0,
                  userId = user.id,
                  accessToken = accessTokenResponse.accessToken,
                  tokenType = accessTokenResponse.tokenType,
                  expiresIn = accessTokenResponse.expiresIn,
                  refreshToken = accessTokenResponse.refreshToken,
                  createdAt = now,
                  updatedAt = now,
                  deletedAt = None,
                  lockVersion = 0
                )
              )
            }
            // ユーザーセッションを発行
            val userSession = SamoyedUserSession(
              id = 0,
              userId = user.id,
              sessionToken = TokenGenerator.generate(s"${user.id}+${now.toEpochSecond}"),
              expiresIn = SamoyedUserSession.EXPIRES_IN,
              createdAt = OffsetDateTime.now(),
              updatedAt = OffsetDateTime.now(),
              deletedAt = None,
              lockVersion = 0
            )
            transaction.write { session =>
              given DBSession = session
              SamoyedUserSessionWriter.write(userSession)
            }
            // ユーザーセッショントークンをCookieに保存
            sessionCookieAccessor.put(userSession.sessionToken)(Redirect("/"))
        }
      }
    }
  }
}
