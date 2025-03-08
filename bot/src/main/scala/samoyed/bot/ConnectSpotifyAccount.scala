package samoyed.bot

import com.google.inject.{Inject, Singleton}
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.text.{TextInput, TextInputStyle}
import net.dv8tion.jda.api.interactions.modals.Modal
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.oauth.SpotifyOAuth
import samoyed.core.lib.security.TokenGenerator
import samoyed.core.model.config.SpotifyConfig
import samoyed.core.model.db.{SamoyedSession, SamoyedUser, UserSpotifyAccessToken}
import samoyed.logging.Logger
import se.michaelthelin.spotify.SpotifyApi
import sttp.model.Uri

import java.time.OffsetDateTime

@Singleton
class ConnectSpotifyAccount @Inject() (
    spotifyConfig: SpotifyConfig,
    transaction: Transaction,
    spotifyOAuth: SpotifyOAuth
) extends ListenerAdapter
    with Logger {

  import ConnectSpotifyAccount._

  override def onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = {
    if (event.getName == slashCommandName) {
      handleSlashCommand(event)
    }
  }

  override def onModalInteraction(event: ModalInteractionEvent): Unit = {
    if (event.getModalId == modalCustomId) {
      handleModalInteraction(event)
    } else {
      logger.warn("不明なモーダルIDが指定されました。", kv("modal_id", event.getModalId))
      event.deferReply().queue()
    }
  }

  /**
   * スラッシュコマンド処理
   */
  private def handleSlashCommand(event: SlashCommandInteractionEvent): Unit = {
    val now = OffsetDateTime.now()
    val clientId = spotifyConfig.clientId
    val responseType = "code"
    val redirectUri = spotifyConfig.redirectUri
    val state = TokenGenerator.generate(s"${now.toEpochSecond}")
    val codeChallengeSet = TokenGenerator.makeCodeChallenge()
    val codeChallengeMethod = "S256"
    val discordUserId = event.getUser.getId
    val expiresIn = 300 // 5分

    transaction.write { session =>
      SamoyedSession.create(
        clientId = clientId,
        redirectUri = redirectUri,
        responseType = responseType,
        state = state,
        codeVerifier = codeChallengeSet.codeVerifier,
        codeChallengeMethod = codeChallengeMethod,
        expiresIn = expiresIn,
        discordUserId = discordUserId,
        createdAt = now,
        updatedAt = now,
        deletedAt = None,
        lockVersion = 0
      )(session)
    }

    val authorizationUrl = buildAuthorizationUrl(
      clientId = clientId,
      responseType = responseType,
      redirectUri = redirectUri,
      state = state,
      codeChallenge = codeChallengeSet.codeChallenge,
      codeChallengeMethod = codeChallengeMethod
    )

    // モーダル生成・送信
    val modal = buildSpotifyAuthModal(authorizationUrl)
    event.replyModal(modal).queue()
  }

  /**
   * Spotify認可リクエストURLを生成する
   * @return
   */
  private def buildAuthorizationUrl(
      clientId: String,
      responseType: String,
      redirectUri: String,
      state: String,
      codeChallenge: String,
      codeChallengeMethod: String
  ): String = {
    val authorizationEndpoint = "https://accounts.spotify.com/authorize"
    val queryParams = Map(
      "client_id" -> Seq(clientId),
      "response_type" -> Seq(responseType),
      "redirect_uri" -> Seq(redirectUri),
      "state" -> Seq(state),
      "scope" -> Seq("user-read-private"),
      "code_challenge" -> Seq(codeChallenge),
      "code_challenge_method" -> Seq(codeChallengeMethod)
    )
    val queryString = queryParams
      .map { case (k, v) =>
        s"$k=${v.mkString(",")}"
      }
      .mkString("&")

    s"$authorizationEndpoint?$queryString"
  }

  /**
   * モーダルを生成する
   * @param authorizationUrl Spotify認可リクエストURL
   * @return
   */
  private def buildSpotifyAuthModal(authorizationUrl: String): Modal = {
    val authorizationUrlTextInput = TextInput
      .create(
        textInputAuthorizationRequestUrlId,
        "連携URL",
        TextInputStyle.SHORT
      )
      .setLabel("Spotifyアカウント連携URLをコピー後、ブラウザで開いてください")
      .setValue(authorizationUrl)
      .build()

    val authorizationResponseUrlTextInput = TextInput
      .create(
        textInputAuthorizationResponseUrlId,
        "連携完了URL",
        TextInputStyle.SHORT
      )
      .setLabel("連携完了後のURLをコピー後、入力してください")
      .build()

    Modal
      .create(modalCustomId, "Spotifyアカウント連携")
      .addActionRow(authorizationUrlTextInput)
      .addActionRow(authorizationResponseUrlTextInput)
      .build()
  }

  /**
   * モーダルの入力を処理する
   */
  private def handleModalInteraction(event: ModalInteractionEvent): Unit = {
    val authorizationResponseUrlOpt =
      Option(event.getValue(textInputAuthorizationResponseUrlId))
        .map(_.getAsString)

    authorizationResponseUrlOpt match {
      case None =>
        event.reply("フォームの形式が不正です。").queue()
      case Some(authorizationResponseUrl) =>
        Uri.parse(authorizationResponseUrl) match {
          case Left(_) =>
            event.reply("URLの形式が不正です。").queue()
          case Right(uri) =>
            val code = uri.params.get("code")
            val state = uri.params.get("state")

            (code, state) match {
              case (Some(c), Some(s)) =>
                completeSpotifyConnection(event, c, s)
              case _ =>
                logger.info("codeもしくはstateが見つかりません。")
                event.reply("URLの形式が不正です。必須パラメーターが見つかりませんでした。").queue()
            }
        }
    }
  }

  /** コード・ステートを用いてSpotify連携を完了させる */
  private def completeSpotifyConnection(
      event: ModalInteractionEvent,
      code: String,
      state: String
  ): Unit = {
    val discordUserId = event.getUser.getId
    val samoyedSessionOpt = transaction.read { session =>
      import scalikejdbc._
      SamoyedSession.findBy(
        sqls
          .eq(SamoyedSession.column.discordUserId, discordUserId)
          .and
          .eq(SamoyedSession.column.state, state)
      )(session)
    }

    samoyedSessionOpt match {
      case None =>
        event.reply("処理中の連携が見つかりませんでした。").queue()
      case Some(samoyedSession) =>
        spotifyOAuth
          .accessTokenRequest(
            redirectUri = spotifyConfig.redirectUri,
            authCode = code,
            codeVerifier = samoyedSession.codeVerifier
          )
          .fold(
            e => {
              logger.error("アクセストークンリクエストに失敗しました。", e)
              event.reply("連携処理が失敗しました。").queue()
            },
            accessTokenResponse => {
              val now = OffsetDateTime.now()
              saveSpotifyAccessToken(samoyedSession, accessTokenResponse, discordUserId, now) match {
                case Left(errMsg) =>
                  event.reply(errMsg).queue()
                case Right(_) =>
                  event.reply("Spotifyアカウントの連携が完了しました。").queue()
              }
            }
          )
    }
  }

  /** Spotify アクセストークン＆ユーザー情報保存処理 */
  private def saveSpotifyAccessToken(
      samoyedSession: SamoyedSession,
      accessTokenResponse: SpotifyOAuth.AccessTokenResponse,
      discordUserId: String,
      now: OffsetDateTime
  ): Either[String, Unit] = {
    try {
      val spotifyUserId = new SpotifyApi.Builder()
        .setClientId(spotifyConfig.clientId)
        .setClientSecret(spotifyConfig.clientSecret)
        .setAccessToken(accessTokenResponse.accessToken)
        .build()
        .getCurrentUsersProfile
        .build()
        .execute()
        .getId

      transaction.write { session =>
        import scalikejdbc._

        // ユーザーが存在しない場合は作成
        val user = SamoyedUser
          .findBy(
            sqls
              .eq(SamoyedUser.column.discordUserId, discordUserId)
              .and
              .eq(SamoyedUser.column.spotifyUserId, spotifyUserId)
              .and
              .isNull(SamoyedUser.column.deletedAt)
          )(session)
          .getOrElse {
            logger.info("ユーザーを新規に作成します。")
            SamoyedUser.create(
              discordUserId = discordUserId,
              spotifyUserId = spotifyUserId,
              createdAt = now,
              updatedAt = now,
              deletedAt = None,
              lockVersion = 0
            )(session)
          }

        // アクセストークン作成/更新
        UserSpotifyAccessToken
          .findBy(sqls.eq(UserSpotifyAccessToken.column.samoyedUserId, user.id))(session)
          .fold {
            logger.info("アクセストークンを保存しました。")
            UserSpotifyAccessToken.create(
              samoyedUserId = user.id,
              accessToken = accessTokenResponse.accessToken,
              tokenType = accessTokenResponse.tokenType,
              expiresIn = accessTokenResponse.expiresIn,
              refreshToken = accessTokenResponse.refreshToken,
              createdAt = now,
              updatedAt = now,
              deletedAt = None,
              lockVersion = 0
            )(session)
          } { userSpotifyAccessToken =>
            logger.info("アクセストークンを更新しました。")
            userSpotifyAccessToken
              .copy(
                accessToken = accessTokenResponse.accessToken,
                tokenType = accessTokenResponse.tokenType,
                expiresIn = accessTokenResponse.expiresIn,
                refreshToken = accessTokenResponse.refreshToken,
                updatedAt = now
              )
              .save()(session)
          }

        // 処理が済んだセッションを削除
        samoyedSession.copy(deletedAt = Some(now)).save()(session)
      }
      Right(())
    } catch {
      case e: Throwable =>
        logger.error("Spotify アクセストークンまたはユーザー情報の保存に失敗しました。", e)
        Left("連携処理中にエラーが発生しました。")
    }
  }
}

object ConnectSpotifyAccount {
  val slashCommandName = "connect-spotify-account"
  private val modalCustomId = "samoyed-connect-spotify-account-modal"
  private val textInputAuthorizationRequestUrlId = "samoyed-connect-spotify-account-authorization-url"
  private val textInputAuthorizationResponseUrlId = "samoyed-connect-spotify-account-authorization-response-url"
}
