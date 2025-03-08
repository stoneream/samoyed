package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbumFetchSchedule(
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

  def save()(implicit session: DBSession): ArtistAlbumFetchSchedule = ArtistAlbumFetchSchedule.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ArtistAlbumFetchSchedule.destroy(this)(session)

}

object ArtistAlbumFetchSchedule extends SQLSyntaxSupport[ArtistAlbumFetchSchedule] {

  override val schemaName = Some("samoyed")

  override val tableName = "artist_album_fetch_schedules"

  override val columns = Seq("id", "artist_id", "scheduled_at", "started_at", "finished_at", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(aafs: SyntaxProvider[ArtistAlbumFetchSchedule])(rs: WrappedResultSet): ArtistAlbumFetchSchedule = autoConstruct(rs, aafs)
  def apply(aafs: ResultName[ArtistAlbumFetchSchedule])(rs: WrappedResultSet): ArtistAlbumFetchSchedule = autoConstruct(rs, aafs)

  val aafs = ArtistAlbumFetchSchedule.syntax("aafs")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[ArtistAlbumFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedule as aafs).where.eq(aafs.id, id)
    }.map(ArtistAlbumFetchSchedule(aafs.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArtistAlbumFetchSchedule] = {
    withSQL(select.from(ArtistAlbumFetchSchedule as aafs)).map(ArtistAlbumFetchSchedule(aafs.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArtistAlbumFetchSchedule as aafs)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArtistAlbumFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedule as aafs).where.append(where)
    }.map(ArtistAlbumFetchSchedule(aafs.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArtistAlbumFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumFetchSchedule as aafs).where.append(where)
    }.map(ArtistAlbumFetchSchedule(aafs.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArtistAlbumFetchSchedule as aafs).where.append(where)
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
  )(implicit session: DBSession): ArtistAlbumFetchSchedule = {
    val generatedKey = withSQL {
      insert
        .into(ArtistAlbumFetchSchedule)
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

    ArtistAlbumFetchSchedule(
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

  def batchInsert(entities: collection.Seq[ArtistAlbumFetchSchedule])(implicit session: DBSession): List[Int] = {
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

  def save(entity: ArtistAlbumFetchSchedule)(implicit session: DBSession): ArtistAlbumFetchSchedule = {
    withSQL {
      update(ArtistAlbumFetchSchedule)
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

  def destroy(entity: ArtistAlbumFetchSchedule)(implicit session: DBSession): Int = {
    withSQL { delete.from(ArtistAlbumFetchSchedule).where.eq(column.id, entity.id) }.update.apply()
  }

}
