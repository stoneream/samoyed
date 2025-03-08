package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserReleaseNotification(
    id: Long,
    samoyedUserId: Long,
    artistAlbumId: Long,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): UserReleaseNotification = UserReleaseNotification.save(this)(session)

  def destroy()(implicit session: DBSession): Int = UserReleaseNotification.destroy(this)(session)

}

object UserReleaseNotification extends SQLSyntaxSupport[UserReleaseNotification] {

  override val schemaName = Some("samoyed")

  override val tableName = "user_release_notifications"

  override val columns = Seq("id", "samoyed_user_id", "artist_album_id", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(urn: SyntaxProvider[UserReleaseNotification])(rs: WrappedResultSet): UserReleaseNotification = autoConstruct(rs, urn)
  def apply(urn: ResultName[UserReleaseNotification])(rs: WrappedResultSet): UserReleaseNotification = autoConstruct(rs, urn)

  val urn = UserReleaseNotification.syntax("urn")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[UserReleaseNotification] = {
    withSQL {
      select.from(UserReleaseNotification as urn).where.eq(urn.id, id)
    }.map(UserReleaseNotification(urn.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[UserReleaseNotification] = {
    withSQL(select.from(UserReleaseNotification as urn)).map(UserReleaseNotification(urn.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(UserReleaseNotification as urn)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[UserReleaseNotification] = {
    withSQL {
      select.from(UserReleaseNotification as urn).where.append(where)
    }.map(UserReleaseNotification(urn.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[UserReleaseNotification] = {
    withSQL {
      select.from(UserReleaseNotification as urn).where.append(where)
    }.map(UserReleaseNotification(urn.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(UserReleaseNotification as urn).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      samoyedUserId: Long,
      artistAlbumId: Long,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): UserReleaseNotification = {
    val generatedKey = withSQL {
      insert
        .into(UserReleaseNotification)
        .namedValues(
          column.samoyedUserId -> samoyedUserId,
          column.artistAlbumId -> artistAlbumId,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    UserReleaseNotification(
      id = generatedKey,
      samoyedUserId = samoyedUserId,
      artistAlbumId = artistAlbumId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[UserReleaseNotification])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "samoyedUserId" -> entity.samoyedUserId,
        "artistAlbumId" -> entity.artistAlbumId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into user_release_notifications(
      samoyed_user_id,
      artist_album_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {samoyedUserId},
      {artistAlbumId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: UserReleaseNotification)(implicit session: DBSession): UserReleaseNotification = {
    withSQL {
      update(UserReleaseNotification)
        .set(
          column.id -> entity.id,
          column.samoyedUserId -> entity.samoyedUserId,
          column.artistAlbumId -> entity.artistAlbumId,
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

  def destroy(entity: UserReleaseNotification)(implicit session: DBSession): Int = {
    withSQL { delete.from(UserReleaseNotification).where.eq(column.id, entity.id) }.update.apply()
  }

}
