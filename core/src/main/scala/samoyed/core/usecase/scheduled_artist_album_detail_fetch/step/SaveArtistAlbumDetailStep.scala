package samoyed.core.usecase.scheduled_artist_album_detail_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.date.DateTimeFormat
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{ArtistAlbum, ArtistAlbumDetail, ArtistAlbumDetailFetchSchedule}
import samoyed.logging.Logger
import scalikejdbc.*
import se.michaelthelin.spotify.enums.ReleaseDatePrecision
import se.michaelthelin.spotify.model_objects.specification.Album

import java.time.{LocalDate, OffsetDateTime}
import scala.util.control.Exception.allCatch

@Singleton
private[scheduled_artist_album_detail_fetch] class SaveArtistAlbumDetailStep @Inject() (
    tx: Transaction
) extends Logger {
  private val dtParser = (s: String) => allCatch.either(LocalDate.parse(s, DateTimeFormat.ymd))

  def run(ls: List[(ArtistAlbumDetailFetchSchedule, ArtistAlbum, Option[Album])], now: OffsetDateTime): Task[Unit] = {
    // 取得できたアルバム詳細について行を作成
    val details = ls.collect { case (_, artistAlbum, Some(album)) =>
      val releasedAt = album.getReleaseDatePrecision match {
        case ReleaseDatePrecision.YEAR => dtParser(s"${album.getReleaseDate}-01-01")
        case ReleaseDatePrecision.MONTH => dtParser(s"${album.getReleaseDate}-01")
        case ReleaseDatePrecision.DAY => dtParser(album.getReleaseDate)
      } match {
        // precisionで振り分けてもエラーになることがある
        // パースに失敗してしまった場合には9999-12-31として扱う
        case Right(dt) => dt
        case Left(e) =>
          error(s"Failed to parse release date: ${album.getReleaseDate}", e)
          LocalDate.of(9999, 12, 31)
      }

      ArtistAlbumDetail(
        id = 0,
        artistAlbumId = artistAlbum.id,
        albumName = album.getName,
        releaseDate = releasedAt,
        releaseDateType = album.getReleaseDatePrecision.precision,
        label = album.getLabel,
        createdAt = now,
        updatedAt = now,
        deletedAt = None
      )
    }

    tx.write { implicit session =>
      val column = ArtistAlbumDetail.column
      val builder = BatchParamsBuilder {
        details.map { detail =>
          Seq(
            column.artistAlbumId -> detail.artistAlbumId,
            column.albumName -> detail.albumName,
            column.releaseDate -> detail.releaseDate,
            column.releaseDateType -> detail.releaseDateType,
            column.label -> detail.label,
            column.createdAt -> detail.createdAt,
            column.updatedAt -> detail.updatedAt,
            column.deletedAt -> detail.deletedAt
          )
        }
      }
      withSQL {
        insertInto(ArtistAlbumDetail).namedValues(builder.columnsAndPlaceholders*)
      }.batch(builder.batchParams*).apply()

      info("Saved artist album details ()", kv("count", details.size))
    }
  }
}
