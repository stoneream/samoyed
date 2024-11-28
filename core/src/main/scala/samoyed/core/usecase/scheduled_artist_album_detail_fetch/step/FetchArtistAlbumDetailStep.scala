package samoyed.core.usecase.scheduled_artist_album_detail_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import samoyed.core.lib.spotify.SpotifyApiErrorHandler.retryTooManyRequests
import samoyed.core.model.config.SpotifyConfig
import samoyed.core.model.db.{ArtistAlbum, ArtistAlbumDetailFetchSchedule}
import samoyed.logging.Logger
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.model_objects.specification.Album

@Singleton
class FetchArtistAlbumDetailStep @Inject() (
    spotifyConfig: SpotifyConfig
) extends Logger {

  def run(
      scheduleWithArtistAlbums: List[(ArtistAlbumDetailFetchSchedule, ArtistAlbum)]
  ): Task[List[(ArtistAlbumDetailFetchSchedule, ArtistAlbum, Option[Album])]] = Task {
    val spotifyApi = new SpotifyApi.Builder()
      .setClientId(spotifyConfig.clientId)
      .setClientSecret(spotifyConfig.clientSecret)
      .build()

    val clientCredentials = spotifyApi.clientCredentials().build().execute()
    spotifyApi.setAccessToken(clientCredentials.getAccessToken)

    val ids = scheduleWithArtistAlbums.map { case (_, artistAlbum) =>
      artistAlbum.spotifyAlbumId
    }

    val albumMap = fetch(ids)(spotifyApi).map { album => (album.getId, album) }.toMap

    scheduleWithArtistAlbums.map { case (schedule, artistAlbum) =>
      (schedule, artistAlbum, albumMap.get(artistAlbum.spotifyAlbumId))
    }
  }

  private def fetch(ids: List[String])(spotifyApi: SpotifyApi): List[Album] = {
    val request = spotifyApi.getSeveralAlbums(ids*).build()
    val value = retryTooManyRequests(Task(request.execute()), 10).runSyncUnsafe()
    value.toList
  }

}
