package samoyed.core.usecase.send_notification

import club.minnced.discord.webhook.WebhookClient
import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.traced
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.util.ErrorHandler.retry
import samoyed.core.model.config.DiscordConfig
import samoyed.core.model.db.{Artist, ArtistAlbum, ArtistAlbumDetail, ReleaseNotification}
import samoyed.logging.Logger
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
class SendNotification @Inject() (
    tx: Transaction,
    discordConfig: DiscordConfig
) extends Logger {
  type Input = SendNotificationInput
  type Output = SendNotificationOutput
  type Exception = SendNotificationException

  private val rn = ReleaseNotification.syntax("rn")
  private val aa = ArtistAlbum.syntax("aa")
  private val aad = ArtistAlbumDetail.syntax("aad")
  private val a = Artist.syntax("a")

  def run(input: Input): Cancelable = {
    val webhookClient = WebhookClient.withUrl(discordConfig.webhookUrl)

    tx.readForWrite { implicit s =>
      val records = collectReleaseNotification
      val messages = buildMessage(records)
      sendMessage(messages)(webhookClient)
      updateNotification(OffsetDateTime.now, records.map(_._1))
      kv("count", records.size)
    }.runAsync {
      case Right(params) =>
        info("sent notification ({})", params)
        SendNotificationOutput()
      case Left(e) => error("failed to send notification", e)
    }
  }

  private def collectReleaseNotification(using DBSession): List[(ReleaseNotification, ArtistAlbum, ArtistAlbumDetail, Artist)] = {
    // 未通知のリリース通知を取得
    withSQL {
      select(
        rn.result.*,
        aa.result.*,
        aad.result.*,
        a.result.*
      )
        .from(ReleaseNotification as rn)
        .join(ArtistAlbum as aa)
        .on(rn.artistAlbumId, aa.id)
        .join(ArtistAlbumDetail as aad)
        .on(aa.id, aad.artistAlbumId)
        .join(Artist as a)
        .on(aa.artistId, a.id)
        .where
        .isNull(rn.notificationSentAt)
        .and
        .isNull(rn.deletedAt)
        .and
        .isNull(aa.deletedAt)
        .and
        .isNull(a.deletedAt)
        .orderBy(rn.createdAt)
    }.map(rs =>
      val releaseNotification = ReleaseNotification(rn.resultName)(rs)
      val artistAlbum = ArtistAlbum(aa.resultName)(rs)
      val artistAlbumDetail = ArtistAlbumDetail(aad.resultName)(rs)
      val artist = Artist(a.resultName)(rs)

      (releaseNotification, artistAlbum, artistAlbumDetail, artist)
    ).list
      .apply()
  }

  private def buildMessage(ls: List[(ReleaseNotification, ArtistAlbum, ArtistAlbumDetail, Artist)]): List[String] = {
    ls.map { case (rn, aa, aad, a) =>
      val albumName = aad.albumName
      val artistName = a.name
      val link = s"https://open.spotify.com/album/${aa.spotifyAlbumId}"

      // markdown
      s"- ${artistName} - ${albumName} ([open](${link}))"
    }.grouped(10) // 10件ずつ送信
      .map(_.mkString("\n"))
      .toList
  }

  private def sendMessage(messages: List[String])(webhookClient: WebhookClient): Unit = {
    messages.zipWithIndex.foreach { case (message, i) =>
      retry(
        Task {
          webhookClient.send(message).join()
          info(
            "send message ({}, {})",
            kv("current", i + 1),
            kv("total", messages.size)
          )
        },
        3000,
        10
      ).runSyncUnsafe()
    }
  }

  private def updateNotification(now: OffsetDateTime, ls: List[ReleaseNotification])(using DBSession): Int = {
    val ids = ls.map(_.id)

    withSQL {
      update(ReleaseNotification as rn)
        .set(rn.notificationSentAt -> now)
        .where
        .in(rn.id, ids)
    }.update.apply()
  }
}
