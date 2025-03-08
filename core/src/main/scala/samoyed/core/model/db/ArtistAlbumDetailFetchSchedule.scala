package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class ArtistAlbumDetailFetchSchedule(
    id: Long,
    artistAlbumId: Long,
    scheduledAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime] = None,
    finishedAt: Option[OffsetDateTime] = None,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): ArtistAlbumDetailFetchSchedule = ArtistAlbumDetailFetchSchedule.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ArtistAlbumDetailFetchSchedule.destroy(this)(session)

}

object ArtistAlbumDetailFetchSchedule extends SQLSyntaxSupport[ArtistAlbumDetailFetchSchedule] {

  override val schemaName = Some("samoyed")

  override val tableName = "artist_album_detail_fetch_schedules"

  override val columns = Seq("id", "artist_album_id", "scheduled_at", "started_at", "finished_at", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(aadfs: SyntaxProvider[ArtistAlbumDetailFetchSchedule])(rs: WrappedResultSet): ArtistAlbumDetailFetchSchedule = autoConstruct(rs, aadfs)
  def apply(aadfs: ResultName[ArtistAlbumDetailFetchSchedule])(rs: WrappedResultSet): ArtistAlbumDetailFetchSchedule = autoConstruct(rs, aadfs)

  val aadfs = ArtistAlbumDetailFetchSchedule.syntax("aadfs")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[ArtistAlbumDetailFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumDetailFetchSchedule as aadfs).where.eq(aadfs.id, id)
    }.map(ArtistAlbumDetailFetchSchedule(aadfs.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArtistAlbumDetailFetchSchedule] = {
    withSQL(select.from(ArtistAlbumDetailFetchSchedule as aadfs)).map(ArtistAlbumDetailFetchSchedule(aadfs.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArtistAlbumDetailFetchSchedule as aadfs)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArtistAlbumDetailFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumDetailFetchSchedule as aadfs).where.append(where)
    }.map(ArtistAlbumDetailFetchSchedule(aadfs.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArtistAlbumDetailFetchSchedule] = {
    withSQL {
      select.from(ArtistAlbumDetailFetchSchedule as aadfs).where.append(where)
    }.map(ArtistAlbumDetailFetchSchedule(aadfs.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArtistAlbumDetailFetchSchedule as aadfs).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      artistAlbumId: Long,
      scheduledAt: OffsetDateTime,
      startedAt: Option[OffsetDateTime] = None,
      finishedAt: Option[OffsetDateTime] = None,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): ArtistAlbumDetailFetchSchedule = {
    val generatedKey = withSQL {
      insert
        .into(ArtistAlbumDetailFetchSchedule)
        .namedValues(
          column.artistAlbumId -> artistAlbumId,
          column.scheduledAt -> scheduledAt,
          column.startedAt -> startedAt,
          column.finishedAt -> finishedAt,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    ArtistAlbumDetailFetchSchedule(
      id = generatedKey,
      artistAlbumId = artistAlbumId,
      scheduledAt = scheduledAt,
      startedAt = startedAt,
      finishedAt = finishedAt,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[ArtistAlbumDetailFetchSchedule])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "artistAlbumId" -> entity.artistAlbumId,
        "scheduledAt" -> entity.scheduledAt,
        "startedAt" -> entity.startedAt,
        "finishedAt" -> entity.finishedAt,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into artist_album_detail_fetch_schedules(
      artist_album_id,
      scheduled_at,
      started_at,
      finished_at,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {artistAlbumId},
      {scheduledAt},
      {startedAt},
      {finishedAt},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ArtistAlbumDetailFetchSchedule)(implicit session: DBSession): ArtistAlbumDetailFetchSchedule = {
    withSQL {
      update(ArtistAlbumDetailFetchSchedule)
        .set(
          column.id -> entity.id,
          column.artistAlbumId -> entity.artistAlbumId,
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

  def destroy(entity: ArtistAlbumDetailFetchSchedule)(implicit session: DBSession): Int = {
    withSQL { delete.from(ArtistAlbumDetailFetchSchedule).where.eq(column.id, entity.id) }.update.apply()
  }

}
