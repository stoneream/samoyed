package samoyed.core.model.config

case class SpotifyConfig(
    clientId: String,
    clientSecret: String,
    redirectUri: SpotifyConfig.RedirectUri
)

object SpotifyConfig {
  case class RedirectUri(
      login: String
  )
}
