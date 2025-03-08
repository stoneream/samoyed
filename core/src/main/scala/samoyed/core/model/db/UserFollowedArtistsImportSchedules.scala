package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtistsImportSchedules(
    id: Long,
    samoyedUserId: Long,
    queuedAt: OffsetDateTime,
    startedAt: Option[OffsetDateTime] = None,
    finishedAt: Option[OffsetDateTime] = None,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): UserFollowedArtistsImportSchedules = UserFollowedArtistsImportSchedules.save(this)(session)

  def destroy()(implicit session: DBSession): Int = UserFollowedArtistsImportSchedules.destroy(this)(session)

}

object UserFollowedArtistsImportSchedules extends SQLSyntaxSupport[UserFollowedArtistsImportSchedules] {

  override val schemaName = Some("samoyed")

  override val tableName = "user_followed_artists_import_schedules"

  override val columns = Seq("id", "samoyed_user_id", "queued_at", "started_at", "finished_at", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(ufais: SyntaxProvider[UserFollowedArtistsImportSchedules])(rs: WrappedResultSet): UserFollowedArtistsImportSchedules = autoConstruct(rs, ufais)
  def apply(ufais: ResultName[UserFollowedArtistsImportSchedules])(rs: WrappedResultSet): UserFollowedArtistsImportSchedules = autoConstruct(rs, ufais)

  val ufais = UserFollowedArtistsImportSchedules.syntax("ufais")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[UserFollowedArtistsImportSchedules] = {
    withSQL {
      select.from(UserFollowedArtistsImportSchedules as ufais).where.eq(ufais.id, id)
    }.map(UserFollowedArtistsImportSchedules(ufais.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[UserFollowedArtistsImportSchedules] = {
    withSQL(select.from(UserFollowedArtistsImportSchedules as ufais)).map(UserFollowedArtistsImportSchedules(ufais.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(UserFollowedArtistsImportSchedules as ufais)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[UserFollowedArtistsImportSchedules] = {
    withSQL {
      select.from(UserFollowedArtistsImportSchedules as ufais).where.append(where)
    }.map(UserFollowedArtistsImportSchedules(ufais.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[UserFollowedArtistsImportSchedules] = {
    withSQL {
      select.from(UserFollowedArtistsImportSchedules as ufais).where.append(where)
    }.map(UserFollowedArtistsImportSchedules(ufais.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(UserFollowedArtistsImportSchedules as ufais).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      samoyedUserId: Long,
      queuedAt: OffsetDateTime,
      startedAt: Option[OffsetDateTime] = None,
      finishedAt: Option[OffsetDateTime] = None,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): UserFollowedArtistsImportSchedules = {
    val generatedKey = withSQL {
      insert
        .into(UserFollowedArtistsImportSchedules)
        .namedValues(
          column.samoyedUserId -> samoyedUserId,
          column.queuedAt -> queuedAt,
          column.startedAt -> startedAt,
          column.finishedAt -> finishedAt,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    UserFollowedArtistsImportSchedules(
      id = generatedKey,
      samoyedUserId = samoyedUserId,
      queuedAt = queuedAt,
      startedAt = startedAt,
      finishedAt = finishedAt,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[UserFollowedArtistsImportSchedules])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "samoyedUserId" -> entity.samoyedUserId,
        "queuedAt" -> entity.queuedAt,
        "startedAt" -> entity.startedAt,
        "finishedAt" -> entity.finishedAt,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into user_followed_artists_import_schedules(
      samoyed_user_id,
      queued_at,
      started_at,
      finished_at,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {samoyedUserId},
      {queuedAt},
      {startedAt},
      {finishedAt},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: UserFollowedArtistsImportSchedules)(implicit session: DBSession): UserFollowedArtistsImportSchedules = {
    withSQL {
      update(UserFollowedArtistsImportSchedules)
        .set(
          column.id -> entity.id,
          column.samoyedUserId -> entity.samoyedUserId,
          column.queuedAt -> entity.queuedAt,
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

  def destroy(entity: UserFollowedArtistsImportSchedules)(implicit session: DBSession): Int = {
    withSQL { delete.from(UserFollowedArtistsImportSchedules).where.eq(column.id, entity.id) }.update.apply()
  }

}
