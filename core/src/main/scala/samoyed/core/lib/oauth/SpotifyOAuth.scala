package samoyed.core.lib.oauth

import com.google.inject.{Inject, Singleton}
import io.circe
import io.circe.derivation.{Configuration, ConfiguredCodec}
import samoyed.core.lib.oauth.SpotifyOAuth.AccessTokenResponse
import samoyed.core.model.config.SpotifyConfig
import sttp.client3.{basicRequest, ResponseException, UriContext}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.client3.circe.asJson

@Singleton
class SpotifyOAuth @Inject() (
    spotifyConfig: SpotifyConfig
) {
  def accessTokenRequest(
      redirectUri: String,
      authCode: String,
      codeVerifier: String
  ): Either[ResponseException[String, circe.Error], AccessTokenResponse] = {
    // https://developer.spotify.com/documentation/web-api/tutorials/code-flow
    val request = basicRequest
      .header("Content-Type", "application/x-www-form-urlencoded")
      .body(
        Map(
          "client_id" -> spotifyConfig.clientId,
          "grant_type" -> "authorization_code",
          "code" -> authCode,
          "redirect_uri" -> redirectUri,
          "code_verifier" -> codeVerifier
        )
      )
      .post(uri"https://accounts.spotify.com/api/token")
      .response(asJson[AccessTokenResponse])

    val backend = OkHttpSyncBackend()
    val response = request.send(backend)

    response.body
  }
}

object SpotifyOAuth {

  object AccessTokenResponse {
    given Configuration = Configuration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames
  }

  case class AccessTokenResponse(
      accessToken: String,
      tokenType: String,
      scope: String,
      expiresIn: Int,
      refreshToken: String
  ) derives ConfiguredCodec
}
