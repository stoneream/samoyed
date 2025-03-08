package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbum(
    id: Long,
    artistId: Long,
    spotifyAlbumId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): ArtistAlbum = ArtistAlbum.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ArtistAlbum.destroy(this)(session)

}

object ArtistAlbum extends SQLSyntaxSupport[ArtistAlbum] {

  override val schemaName = Some("samoyed")

  override val tableName = "artist_albums"

  override val columns = Seq("id", "artist_id", "spotify_album_id", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(aa: SyntaxProvider[ArtistAlbum])(rs: WrappedResultSet): ArtistAlbum = autoConstruct(rs, aa)
  def apply(aa: ResultName[ArtistAlbum])(rs: WrappedResultSet): ArtistAlbum = autoConstruct(rs, aa)

  val aa = ArtistAlbum.syntax("aa")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[ArtistAlbum] = {
    withSQL {
      select.from(ArtistAlbum as aa).where.eq(aa.id, id)
    }.map(ArtistAlbum(aa.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArtistAlbum] = {
    withSQL(select.from(ArtistAlbum as aa)).map(ArtistAlbum(aa.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArtistAlbum as aa)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArtistAlbum] = {
    withSQL {
      select.from(ArtistAlbum as aa).where.append(where)
    }.map(ArtistAlbum(aa.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArtistAlbum] = {
    withSQL {
      select.from(ArtistAlbum as aa).where.append(where)
    }.map(ArtistAlbum(aa.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArtistAlbum as aa).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      artistId: Long,
      spotifyAlbumId: String,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): ArtistAlbum = {
    val generatedKey = withSQL {
      insert
        .into(ArtistAlbum)
        .namedValues(
          column.artistId -> artistId,
          column.spotifyAlbumId -> spotifyAlbumId,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    ArtistAlbum(
      id = generatedKey,
      artistId = artistId,
      spotifyAlbumId = spotifyAlbumId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[ArtistAlbum])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "artistId" -> entity.artistId,
        "spotifyAlbumId" -> entity.spotifyAlbumId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into artist_albums(
      artist_id,
      spotify_album_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {artistId},
      {spotifyAlbumId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ArtistAlbum)(implicit session: DBSession): ArtistAlbum = {
    withSQL {
      update(ArtistAlbum)
        .set(
          column.id -> entity.id,
          column.artistId -> entity.artistId,
          column.spotifyAlbumId -> entity.spotifyAlbumId,
          column.createdAt -> entity.createdAt,
          column.updatedAt -> entity.updatedAt,
          column.deletedAt -> entity.deletedAt,
          column.lockVersion -> entity.lockVersion
        )
        .where
        .eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArtistAlbum)(implicit session: DBSession): Int = {
    withSQL { delete.from(ArtistAlbum).where.eq(column.id, entity.id) }.update.apply()
  }

}
