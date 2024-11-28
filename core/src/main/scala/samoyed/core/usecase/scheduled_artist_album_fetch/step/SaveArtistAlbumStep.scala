package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{Artist, ArtistAlbum}
import scalikejdbc.*
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified

import java.time.OffsetDateTime

@Singleton
private[scheduled_artist_album_fetch] class SaveArtistAlbumStep @Inject() (
    tx: Transaction
) {
  def run(artistWithNewAlbums: (Artist, List[AlbumSimplified]), now: OffsetDateTime): Task[Unit] = {
    val (artist, newAlbums) = artistWithNewAlbums
    // 新規アルバムの行を作成
    val rows = makeRows(artist, newAlbums, now)

    for {
      // 新規アルバムをDBに登録
      _ <- insertAlbums(rows)
    } yield {}
  }

  private def makeRows(artist: Artist, albums: List[AlbumSimplified], now: OffsetDateTime): List[ArtistAlbum] = {
    albums.map { album =>
      ArtistAlbum(
        id = 0,
        artistId = artist.id,
        spotifyAlbumId = album.getId,
        createdAt = now,
        updatedAt = now,
        deletedAt = None
      )
    }
  }

  private def insertAlbums(albums: List[ArtistAlbum]): Task[Unit] = {
    tx.write { implicit session =>
      val column = ArtistAlbum.column
      val builder = BatchParamsBuilder {
        albums.map { album =>
          Seq(
            column.artistId -> album.artistId,
            column.spotifyAlbumId -> album.spotifyAlbumId,
            column.createdAt -> album.createdAt,
            column.updatedAt -> album.updatedAt,
            column.deletedAt -> album.deletedAt
          )
        }
      }
      withSQL {
        insertInto(ArtistAlbum).namedValues(builder.columnsAndPlaceholders*)
      }.batch(builder.batchParams*).apply()
    }
  }
}
