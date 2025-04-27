package database

import scalikejdbc._
import java.time.{OffsetDateTime}

case class ArtistAlbumDetail(
  id: Int,
  artistAlbumId: Int,
  albumName: String,
  releaseDate: OffsetDateTime,
  releaseDateType: String,
  label: String,
  createdAt: OffsetDateTime,
  updatedAt: OffsetDateTime,
  deletedAt: Option[OffsetDateTime] = None,
  lockVersion: Int) {

  def save()(implicit session: DBSession): ArtistAlbumDetail = ArtistAlbumDetail.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ArtistAlbumDetail.destroy(this)(session)

}


object ArtistAlbumDetail extends SQLSyntaxSupport[ArtistAlbumDetail] {

  override val schemaName = Some("dev_samoyed")

  override val tableName = "artist_album_detail"

  override val columns = Seq("id", "artist_album_id", "album_name", "release_date", "release_date_type", "label", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(aad: SyntaxProvider[ArtistAlbumDetail])(rs: WrappedResultSet): ArtistAlbumDetail = autoConstruct(rs, aad)
  def apply(aad: ResultName[ArtistAlbumDetail])(rs: WrappedResultSet): ArtistAlbumDetail = autoConstruct(rs, aad)

  val aad = ArtistAlbumDetail.syntax("aad")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ArtistAlbumDetail] = {
    withSQL {
      select.from(ArtistAlbumDetail as aad).where.eq(aad.id, id)
    }.map(ArtistAlbumDetail(aad.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArtistAlbumDetail] = {
    withSQL(select.from(ArtistAlbumDetail as aad)).map(ArtistAlbumDetail(aad.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArtistAlbumDetail as aad)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArtistAlbumDetail] = {
    withSQL {
      select.from(ArtistAlbumDetail as aad).where.append(where)
    }.map(ArtistAlbumDetail(aad.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArtistAlbumDetail] = {
    withSQL {
      select.from(ArtistAlbumDetail as aad).where.append(where)
    }.map(ArtistAlbumDetail(aad.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArtistAlbumDetail as aad).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    artistAlbumId: Int,
    albumName: String,
    releaseDate: OffsetDateTime,
    releaseDateType: String,
    label: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int)(implicit session: DBSession): ArtistAlbumDetail = {
    val generatedKey = withSQL {
      insert.into(ArtistAlbumDetail).namedValues(
        column.artistAlbumId -> artistAlbumId,
        column.albumName -> albumName,
        column.releaseDate -> releaseDate,
        column.releaseDateType -> releaseDateType,
        column.label -> label,
        column.createdAt -> createdAt,
        column.updatedAt -> updatedAt,
        column.deletedAt -> deletedAt,
        column.lockVersion -> lockVersion
      )
    }.updateAndReturnGeneratedKey.apply()

    ArtistAlbumDetail(
      id = generatedKey.toInt,
      artistAlbumId = artistAlbumId,
      albumName = albumName,
      releaseDate = releaseDate,
      releaseDateType = releaseDateType,
      label = label,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion)
  }

  def batchInsert(entities: collection.Seq[ArtistAlbumDetail])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "artistAlbumId" -> entity.artistAlbumId,
        "albumName" -> entity.albumName,
        "releaseDate" -> entity.releaseDate,
        "releaseDateType" -> entity.releaseDateType,
        "label" -> entity.label,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion))
    SQL("""insert into artist_album_detail(
      artist_album_id,
      album_name,
      release_date,
      release_date_type,
      label,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {artistAlbumId},
      {albumName},
      {releaseDate},
      {releaseDateType},
      {label},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ArtistAlbumDetail)(implicit session: DBSession): ArtistAlbumDetail = {
    withSQL {
      update(ArtistAlbumDetail).set(
        column.id -> entity.id,
        column.artistAlbumId -> entity.artistAlbumId,
        column.albumName -> entity.albumName,
        column.releaseDate -> entity.releaseDate,
        column.releaseDateType -> entity.releaseDateType,
        column.label -> entity.label,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.deletedAt -> entity.deletedAt,
        column.lockVersion -> entity.lockVersion
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArtistAlbumDetail)(implicit session: DBSession): Int = {
    withSQL { delete.from(ArtistAlbumDetail).where.eq(column.id, entity.id) }.update.apply()
  }

}
