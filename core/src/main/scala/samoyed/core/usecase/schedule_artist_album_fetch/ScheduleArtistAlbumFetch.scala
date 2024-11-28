package samoyed.core.usecase.schedule_artist_album_fetch

import com.google.inject.{Inject, Singleton}
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.traced
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.{Artist, ArtistAlbumFetchSchedule}
import scalikejdbc.*

import java.time.OffsetDateTime

@Singleton
class ScheduleArtistAlbumFetch @Inject() (
    tx: Transaction
) {
  type Input = ScheduleArtistAlbumFetchInput
  type Output = ScheduleArtistAlbumFetchOutput
  type Exception = ScheduleArtistAlbumFetchException

  def run(input: Input): Cancelable = {
    val a = Artist.syntax("a")
    val now = OffsetDateTime.now()
    val task = for {
      artists <- tx.read { implicit session => // 巡回対象のアーティストを洗い出す
        withSQL {
          select
            .from(Artist as a)
            .where
            .isNull(a.deletedAt)
            .orderBy(a.createdAt)
        }.map(Artist(a.resultName)).list.apply()
      }
      rows = artists.map { artist =>
        ArtistAlbumFetchSchedule(
          id = 0,
          artistId = artist.id,
          scheduledAt = now,
          startedAt = None,
          finishedAt = None,
          createdAt = now,
          updatedAt = now,
          deletedAt = None,
          lockVersion = 0
        )
      }
      _ <- tx.write { implicit session => // 行の書き込み
        val column = ArtistAlbumFetchSchedule.column
        val builder = BatchParamsBuilder {
          rows.map { row =>
            Seq(
              column.artistId -> row.artistId,
              column.scheduledAt -> row.scheduledAt,
              column.startedAt -> row.startedAt,
              column.finishedAt -> row.finishedAt,
              column.createdAt -> row.createdAt,
              column.updatedAt -> row.updatedAt,
              column.deletedAt -> row.deletedAt,
              column.lockVersion -> row.lockVersion
            )
          }
        }
        withSQL {
          insert.into(ArtistAlbumFetchSchedule).namedValues(builder.columnsAndPlaceholders*)
        }.batch(builder.batchParams*).apply()
      }
    } yield {
      ScheduleArtistAlbumFetchOutput()
    }

    task.runAsync {
      case Right(_) => ()
      case Left(e) => throw e
    }
  }
}
