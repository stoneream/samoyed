package samoyed.core.usecase.schedule_artist_album_fetch.step

import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.lib.db.Transaction
import samoyed.core.model.db.Artist
import scalikejdbc.*

@Singleton
private [schedule_artist_album_fetch] class FetchArtistStep @Inject() (
    tx: Transaction
) {
  private val a = Artist.syntax("a")

  /**
   * 巡回対象のアーティストを洗い出す
   * @return
   */
  def run(): Task[List[Artist]] = {
    tx.read { implicit session =>
      withSQL {
        select
          .from(Artist as a)
          .where
          .isNull(a.deletedAt)
          .orderBy(a.createdAt)
      }.map(Artist(a.resultName)).list.apply()
    }
  }
}
