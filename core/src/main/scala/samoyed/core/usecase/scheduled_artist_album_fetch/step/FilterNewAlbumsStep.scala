package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{Artist, ArtistAlbum}
import scalikejdbc.*
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified

@Singleton
private[scheduled_artist_album_fetch] class FilterNewAlbumsStep @Inject() (
    tx: Transaction
) {
  private val aa = ArtistAlbum.syntax("aa")

  /**
   * 未登録のアルバムを抽出する
   * @param artistWithAlbums
   * @return
   */
  def run(artistWithAlbums: (Artist, List[AlbumSimplified])): Task[(Artist, List[AlbumSimplified])] = {
    val (artist, albums) = artistWithAlbums

    for {
      registeredAlbums <- getRegisteredAlbums(artist)
      newAlbums <- filter(albums, registeredAlbums)
    } yield (artist, newAlbums)
  }

  private def getRegisteredAlbums(artist: Artist): Task[List[ArtistAlbum]] = {
    tx.read { implicit session =>
      withSQL {
        select
          .from(ArtistAlbum as aa)
          .where
          .eq(aa.artistId, artist.id)
          .and
          .isNull(aa.deletedAt)
      }.map(ArtistAlbum(aa.resultName)).list.apply()
    }
  }

  private def filter(albums: List[AlbumSimplified], registeredAlbums: List[ArtistAlbum]): Task[List[AlbumSimplified]] = Task {
    albums.filter { album => !registeredAlbums.exists(_.spotifyAlbumId == album.getId) }
  }
}
