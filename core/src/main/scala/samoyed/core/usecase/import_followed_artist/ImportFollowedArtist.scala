package samoyed.core.usecase.import_followed_artist

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.core.lib.spotify.SpotifyApiErrorHandler.retryTooManyRequests
import samoyed.core.model.config.SpotifyConfig
import samoyed.core.model.db.Artist
import samoyed.logging.Logger
import scalikejdbc.*
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.enums.ModelObjectType
import se.michaelthelin.spotify.model_objects.specification.Artist as SpotifyArtist
import monix.execution.Scheduler.Implicits.global
import scala.annotation.tailrec

@Singleton
class ImportFollowedArtist @Inject() (
    spotifyConfig: SpotifyConfig
) extends Logger {
  type Input = ImportFollowedArtistInput
  type Output = ImportFollowedArtistOutput
  type Exception = ImportFollowedArtistException

  def run(input: Input): Task[Output] = {
    val client = new SpotifyApi.Builder()
      .setClientId(spotifyConfig.clientId)
      .setClientSecret(spotifyConfig.clientSecret)
      .setAccessToken(input.accessToken)
      .build()

    // フォロー中のアーティストを取得
    val spotifyArtists = fetch(client)
    info(s"Fetched ${spotifyArtists.size} followed artists")

    // すでに登録されているアーティストを除外するため検索
    val a = Artist.syntax("a")

    val spotifyArtistIds = spotifyArtists.map(_.getId)
    val stored = DB.readOnly { implicit s =>
      withSQL {
        select
          .from(Artist as a)
          .where
          .eq(a.deletedAt, None)
          .and
          .in(a.spotifyArtistId, spotifyArtistIds)
      }.map(Artist(a.resultName)).list.apply()
    }

    // 既存のアーティストを除外
    val newArtists = spotifyArtists.filterNot { artist =>
      stored.exists(_.spotifyArtistId == artist.getId)
    }

    // 新規アーティストを登録
    val column = Artist.column
    val builder = BatchParamsBuilder {
      newArtists.map { release =>
        Seq(
          column.name -> release.getName,
          column.spotifyArtistId -> release.getId,
          column.createdAt -> input.now,
          column.updatedAt -> input.now
        )
      }
    }

    DB.localTx { implicit s =>
      withSQL {
        insert.into(Artist).namedValues(builder.columnsAndPlaceholders*)
      }.batch(builder.batchParams*).apply()
    }

    info(s"Stored ${newArtists.size} artists")

    Task(ImportFollowedArtistOutput())
  }

  private def fetch(client: SpotifyApi): List[SpotifyArtist] = {

    /**
     * フォロー中のアーティストを再帰的に取得する
     */
    @tailrec
    def f(artists: List[SpotifyArtist] = Nil, afterOpt: Option[String] = None): List[SpotifyArtist] = {
      val request = afterOpt
        .fold {
          client.getUsersFollowedArtists(ModelObjectType.ARTIST).limit(50)
        } { after =>
          client.getUsersFollowedArtists(ModelObjectType.ARTIST).after(after).limit(50)
        }
        .build()

      val result = retryTooManyRequests(Task(request.execute()), 10).runSyncUnsafe()

      result.getCursors.toList match {
        case Nil =>
          warn("Failed get cursor")
          artists
        case cursor :: _ =>
          val after = cursor.getAfter
          val items = artists ++ result.getItems
          info(
            s"Fetching followed artists",
            kv("progress", s"${items.size}/${result.getTotal}")
          )

          if (after == null) {
            items
          } else {
            f(items, Some(after))
          }
      }
    }
    f()
  }
}
