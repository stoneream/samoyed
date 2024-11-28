package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{Artist, ArtistAlbum, ArtistAlbumDetailFetchSchedule}
import scalikejdbc.*
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_fetch] class CreateScheduleArtistAlbumDetailFetch @Inject() (
    tx: Transaction
) {
  private val aa = ArtistAlbum.syntax("aa")

  def run(artistWithNewAlbums: (Artist, List[AlbumSimplified]), now: OffsetDateTime): Task[Unit] = {
    val (artist, newAlbums) = artistWithNewAlbums

    for {
      artistAlbums <- getNewArtistAlbum(newAlbums)
      rows = makeRows(artistAlbums, now)
      _ <- insertAlbums(rows)
    } yield ()
  }

  private def getNewArtistAlbum(newAlbums: List[AlbumSimplified]): Task[List[ArtistAlbum]] = {
    tx.read { implicit session =>
      withSQL {
        select
          .from(ArtistAlbum as aa)
          .where
          .in(aa.spotifyAlbumId, newAlbums.map(_.getId))
          .and
          .isNull(aa.deletedAt)
      }.map(ArtistAlbum(aa.resultName)).list.apply()
    }
  }

  private def makeRows(artistAlbums: List[ArtistAlbum], now: OffsetDateTime): List[ArtistAlbumDetailFetchSchedule] = {
    artistAlbums.map { artistAlbum =>
      ArtistAlbumDetailFetchSchedule(
        id = 0,
        artistAlbumId = artistAlbum.id,
        scheduledAt = now,
        startedAt = None,
        finishedAt = None,
        createdAt = now,
        updatedAt = now,
        deletedAt = None,
        lockVersion = 0
      )
    }
  }

  private def insertAlbums(albums: List[ArtistAlbumDetailFetchSchedule]): Task[Unit] = {
    tx.write { implicit session =>
      val column = ArtistAlbumDetailFetchSchedule.column
      val builder = BatchParamsBuilder {
        albums.map { album =>
          Seq(
            column.artistAlbumId -> album.artistAlbumId,
            column.scheduledAt -> album.scheduledAt,
            column.createdAt -> album.createdAt,
            column.updatedAt -> album.updatedAt,
            column.deletedAt -> album.deletedAt,
            column.lockVersion -> album.lockVersion
          )
        }
      }
      withSQL {
        insertInto(ArtistAlbumDetailFetchSchedule).namedValues(builder.columnsAndPlaceholders*)
      }.batch(builder.batchParams*).apply()
    }
  }

}
