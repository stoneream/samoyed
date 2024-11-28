package samoyed.core.usecase.scheduled_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.spotify.SpotifyApiErrorHandler.retryTooManyRequests
import samoyed.core.model.config.SpotifyConfig
import samoyed.core.model.db.Artist
import samoyed.logging.Logger
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified

import scala.annotation.tailrec

@Singleton
private[scheduled_artist_album_fetch] class FetchArtistAlbumStep @Inject() (
    spotifyConfig: SpotifyConfig
) extends Logger {
  def run(artist: Artist): Task[(Artist, List[AlbumSimplified])] = Task {
    val spotifyApi = new SpotifyApi.Builder()
      .setClientId(spotifyConfig.clientId)
      .setClientSecret(spotifyConfig.clientSecret)
      .build()

    val clientCredentials = spotifyApi.clientCredentials().build().execute()
    spotifyApi.setAccessToken(clientCredentials.getAccessToken)

    val albums = recursiveFetch(artist)(spotifyApi)

    (artist, albums)
  }

  /**
   * アーティストのリリース情報を再帰的に取得する
   */
  private def recursiveFetch(artist: Artist)(client: SpotifyApi): List[AlbumSimplified] = {
    @tailrec
    def f(releases: List[AlbumSimplified] = Nil, offset: Int = 0): List[AlbumSimplified] = {
      val request = client
        .getArtistsAlbums(artist.spotifyArtistId)
        .limit(50)
        .offset(offset)
        .album_type("album,single")
        .build()

      val value = retryTooManyRequests(Task(request.execute()), 10).runSyncUnsafe()

      val items = releases ++ value.getItems.toList

      info(
        s"Fetching artist releases ({}, {})",
        kv("artist", artist.name),
        kv("progress", s"${items.size}/${value.getTotal}")
      )

      if (value.getNext == null) {
        items
      } else {
        f(items, offset + 50)
      }
    }

    f()
  }
}
