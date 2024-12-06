package samoyed.core.usecase.create_notification

import com.google.inject.{Inject, Singleton}
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.traced
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{ArtistAlbumDetail, MutedLabel, ReleaseNotification}
import samoyed.logging.Logger
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
class CreateNotification @Inject() (
    tx: Transaction
) extends Logger {
  type Input = CreateNotificationInput
  type Output = CreateNotificationOutput
  type Exception = CreateNotificationException

  private val ml = MutedLabel.syntax("ml")
  private val aad = ArtistAlbumDetail.syntax("aad")
  private val rn = ReleaseNotification.syntax("rn")

  def run(input: Input): Cancelable = {
    val now = OffsetDateTime.now
    tx.readForWrite { implicit session =>
      // 通知を除外しているレーベルを取得
      val mutedLabel = fetchMutedLabel()
      // 過去7日間にリリースされたアルバムを取得
      val latestReleases = collectRelease(now, 7)
      // ミュートしているレーベルからのリリースを除外
      val filteredArtistAlbumDetails = filteredMutedLabel(latestReleases, mutedLabel)
      // 取得したアルバムのうち、すでに作成済みの通知を取得
      val releaseNotifications = collectReleaseNotification(filteredArtistAlbumDetails)
      // 未作成の通知情報を抽出
      val newArtistAlbumDetails = filterNewReleaseNotification(filteredArtistAlbumDetails, releaseNotifications)
      // 通知情報を保存
      insertReleaseNotification(newArtistAlbumDetails, now)

      newArtistAlbumDetails.size
    }.runAsync {
      case Right(count) =>
        info("created release notification ({})", kv("count", count))
        CreateNotificationOutput()
      case Left(e) =>
        error("failed to create release notification", e)
        throw e
    }
  }

  private def collectRelease(now: OffsetDateTime, dateRange: Int)(using session: DBSession): List[ArtistAlbumDetail] = {
    // 過去n日間にリリースされたアルバムを取得
    val start = now.minusDays(dateRange)
    val end = now
    withSQL {
      select
        .from(ArtistAlbumDetail as aad)
        .where
        .eq(aad.deletedAt, None)
        .and
        .between(aad.releaseDate, start, end)
    }.map(ArtistAlbumDetail(aad.resultName)).list.apply()
  }

  private def fetchMutedLabel()(using session: DBSession): List[MutedLabel] = {
    // 通知を除外しているレーベルを取得
    withSQL {
      select
        .from(MutedLabel as ml)
        .where
        .eq(ml.deletedAt, None)
    }.map(MutedLabel(ml.resultName)).list.apply()
  }

  private def filteredMutedLabel(
      artistAlbumDetails: List[ArtistAlbumDetail],
      mutedLabels: List[MutedLabel]
  ): List[ArtistAlbumDetail] = {
    // ミュートしているレーベルからのリリースを除外
    // 完全一致ではなく文字列を含む場合に除外
    val mutedLabelNames = mutedLabels.map(_.labelName)
    artistAlbumDetails.filterNot { artistAlbumDetail =>
      mutedLabelNames.exists(artistAlbumDetail.label.contains)
    }
  }

  private def collectReleaseNotification(artistAlbumDetails: List[ArtistAlbumDetail])(using session: DBSession): List[ReleaseNotification] = {
    // 作成済みの通知情報を取得
    withSQL {
      select
        .from(ReleaseNotification as rn)
        .where
        .eq(rn.deletedAt, None)
        .and
        .in(rn.artistAlbumId, artistAlbumDetails.map(_.artistAlbumId))
    }.map(ReleaseNotification(rn.resultName)).list.apply()
  }

  private def filterNewReleaseNotification(
      artistAlbumDetails: List[ArtistAlbumDetail],
      releaseNotifications: List[ReleaseNotification]
  ): List[ArtistAlbumDetail] = {
    val alreadyIds = releaseNotifications.map(_.artistAlbumId).toSet
    val maybeNewIds = artistAlbumDetails.map(_.artistAlbumId).toSet
    // すでに作成済みの通知を除外
    val newIds = maybeNewIds -- alreadyIds
    artistAlbumDetails.filter(artistAlbumDetail => newIds.contains(artistAlbumDetail.artistAlbumId))
  }

  private def insertReleaseNotification(
      newArtistAlbumDetails: List[ArtistAlbumDetail],
      now: OffsetDateTime
  )(using session: DBSession): Unit = {
    // 未作成の通知情報を作成
    val column = ReleaseNotification.column
    val builder = BatchParamsBuilder {
      newArtistAlbumDetails.map { artistAlbumDetail =>
        Seq(
          column.artistAlbumId -> artistAlbumDetail.artistAlbumId,
          column.notificationSentAt -> None,
          column.createdAt -> now,
          column.updatedAt -> now,
          column.deletedAt -> None
        )
      }
    }

    withSQL {
      insert.into(ReleaseNotification).namedValues(builder.columnsAndPlaceholders*)
    }.batch(builder.batchParams*).apply()
  }
}
