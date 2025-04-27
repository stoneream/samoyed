package database

import scalikejdbc._
import java.time.{OffsetDateTime}

case class Artist(
  id: Int,
  name: String,
  spotifyArtistId: String,
  createdAt: OffsetDateTime,
  updatedAt: OffsetDateTime,
  deletedAt: Option[OffsetDateTime] = None,
  lockVersion: Int) {

  def save()(implicit session: DBSession): Artist = Artist.save(this)(session)

  def destroy()(implicit session: DBSession): Int = Artist.destroy(this)(session)

}


object Artist extends SQLSyntaxSupport[Artist] {

  override val schemaName = Some("dev_samoyed")

  override val tableName = "artist"

  override val columns = Seq("id", "name", "spotify_artist_id", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(a: SyntaxProvider[Artist])(rs: WrappedResultSet): Artist = autoConstruct(rs, a)
  def apply(a: ResultName[Artist])(rs: WrappedResultSet): Artist = autoConstruct(rs, a)

  val a = Artist.syntax("a")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[Artist] = {
    withSQL {
      select.from(Artist as a).where.eq(a.id, id)
    }.map(Artist(a.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Artist] = {
    withSQL(select.from(Artist as a)).map(Artist(a.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Artist as a)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Artist] = {
    withSQL {
      select.from(Artist as a).where.append(where)
    }.map(Artist(a.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Artist] = {
    withSQL {
      select.from(Artist as a).where.append(where)
    }.map(Artist(a.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Artist as a).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String,
    spotifyArtistId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int)(implicit session: DBSession): Artist = {
    val generatedKey = withSQL {
      insert.into(Artist).namedValues(
        column.name -> name,
        column.spotifyArtistId -> spotifyArtistId,
        column.createdAt -> createdAt,
        column.updatedAt -> updatedAt,
        column.deletedAt -> deletedAt,
        column.lockVersion -> lockVersion
      )
    }.updateAndReturnGeneratedKey.apply()

    Artist(
      id = generatedKey.toInt,
      name = name,
      spotifyArtistId = spotifyArtistId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion)
  }

  def batchInsert(entities: collection.Seq[Artist])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "name" -> entity.name,
        "spotifyArtistId" -> entity.spotifyArtistId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion))
    SQL("""insert into artist(
      name,
      spotify_artist_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {name},
      {spotifyArtistId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Artist)(implicit session: DBSession): Artist = {
    withSQL {
      update(Artist).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.spotifyArtistId -> entity.spotifyArtistId,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.deletedAt -> entity.deletedAt,
        column.lockVersion -> entity.lockVersion
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Artist)(implicit session: DBSession): Int = {
    withSQL { delete.from(Artist).where.eq(column.id, entity.id) }.update.apply()
  }

}
