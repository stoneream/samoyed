package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbumFetchSchedules(
    id: Long,
    artistId: Long,
    scheduledAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime] = None,
    finishedAt: Option[OffsetDateTime] = None,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): ArtistAlbumFetchSchedules = ArtistAlbumFetchSchedules.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ArtistAlbumFetchSchedules.destroy(this)(session)

}

object ArtistAlbumFetchSchedules extends SQLSyntaxSupport[ArtistAlbumFetchSchedules] {

  override val schemaName = Some("samoyed")

  override val tableName = "artist_album_fetch_schedules"

  override val columns = Seq("id", "artist_id", "scheduled_at", "started_at", "finished_at", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(aafs: SyntaxProvider[ArtistAlbumFetchSchedules])(rs: WrappedResultSet): ArtistAlbumFetchSchedules = autoConstruct(rs, aafs)
  def apply(aafs: ResultName[ArtistAlbumFetchSchedules])(rs: WrappedResultSet): ArtistAlbumFetchSchedules = autoConstruct(rs, aafs)

  val aafs = ArtistAlbumFetchSchedules.syntax("aafs")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[ArtistAlbumFetchSchedules] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedules as aafs).where.eq(aafs.id, id)
    }.map(ArtistAlbumFetchSchedules(aafs.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArtistAlbumFetchSchedules] = {
    withSQL(select.from(ArtistAlbumFetchSchedules as aafs)).map(ArtistAlbumFetchSchedules(aafs.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArtistAlbumFetchSchedules as aafs)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArtistAlbumFetchSchedules] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedules as aafs).where.append(where)
    }.map(ArtistAlbumFetchSchedules(aafs.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArtistAlbumFetchSchedules] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedules as aafs).where.append(where)
    }.map(ArtistAlbumFetchSchedules(aafs.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArtistAlbumFetchSchedules as aafs).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      artistId: Long,
      scheduledAt: OffsetDateTime,
      startedAt: Option[OffsetDateTime] = None,
      finishedAt: Option[OffsetDateTime] = None,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): ArtistAlbumFetchSchedules = {
    val generatedKey = withSQL {
      insert
        .into(ArtistAlbumFetchSchedules)
        .namedValues(
          column.artistId -> artistId,
          column.scheduledAt -> scheduledAt,
          column.startedAt -> startedAt,
          column.finishedAt -> finishedAt,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    ArtistAlbumFetchSchedules(
      id = generatedKey,
      artistId = artistId,
      scheduledAt = scheduledAt,
      startedAt = startedAt,
      finishedAt = finishedAt,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[ArtistAlbumFetchSchedules])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "artistId" -> entity.artistId,
        "scheduledAt" -> entity.scheduledAt,
        "startedAt" -> entity.startedAt,
        "finishedAt" -> entity.finishedAt,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into artist_album_fetch_schedules(
      artist_id,
      scheduled_at,
      started_at,
      finished_at,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {artistId},
      {scheduledAt},
      {startedAt},
      {finishedAt},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ArtistAlbumFetchSchedules)(implicit session: DBSession): ArtistAlbumFetchSchedules = {
    withSQL {
      update(ArtistAlbumFetchSchedules)
        .set(
          column.id -> entity.id,
          column.artistId -> entity.artistId,
          column.scheduledAt -> entity.scheduledAt,
          column.startedAt -> entity.startedAt,
          column.finishedAt -> entity.finishedAt,
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

  def destroy(entity: ArtistAlbumFetchSchedules)(implicit session: DBSession): Int = {
    withSQL { delete.from(ArtistAlbumFetchSchedules).where.eq(column.id, entity.id) }.update.apply()
  }

}
