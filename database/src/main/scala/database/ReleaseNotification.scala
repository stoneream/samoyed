package database

import scalikejdbc._
import java.time.{OffsetDateTime}

case class ReleaseNotification(
  id: Int,
  artistAlbumId: Int,
  notificationSentAt: Option[OffsetDateTime] = None,
  createdAt: OffsetDateTime,
  updatedAt: OffsetDateTime,
  deletedAt: Option[OffsetDateTime] = None,
  lockVersion: Int) {

  def save()(implicit session: DBSession): ReleaseNotification = ReleaseNotification.save(this)(session)

  def destroy()(implicit session: DBSession): Int = ReleaseNotification.destroy(this)(session)

}


object ReleaseNotification extends SQLSyntaxSupport[ReleaseNotification] {

  override val schemaName = Some("dev_samoyed")

  override val tableName = "release_notification"

  override val columns = Seq("id", "artist_album_id", "notification_sent_at", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(rn: SyntaxProvider[ReleaseNotification])(rs: WrappedResultSet): ReleaseNotification = autoConstruct(rs, rn)
  def apply(rn: ResultName[ReleaseNotification])(rs: WrappedResultSet): ReleaseNotification = autoConstruct(rs, rn)

  val rn = ReleaseNotification.syntax("rn")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ReleaseNotification] = {
    withSQL {
      select.from(ReleaseNotification as rn).where.eq(rn.id, id)
    }.map(ReleaseNotification(rn.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ReleaseNotification] = {
    withSQL(select.from(ReleaseNotification as rn)).map(ReleaseNotification(rn.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ReleaseNotification as rn)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ReleaseNotification] = {
    withSQL {
      select.from(ReleaseNotification as rn).where.append(where)
    }.map(ReleaseNotification(rn.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ReleaseNotification] = {
    withSQL {
      select.from(ReleaseNotification as rn).where.append(where)
    }.map(ReleaseNotification(rn.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ReleaseNotification as rn).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    artistAlbumId: Int,
    notificationSentAt: Option[OffsetDateTime] = None,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int)(implicit session: DBSession): ReleaseNotification = {
    val generatedKey = withSQL {
      insert.into(ReleaseNotification).namedValues(
        column.artistAlbumId -> artistAlbumId,
        column.notificationSentAt -> notificationSentAt,
        column.createdAt -> createdAt,
        column.updatedAt -> updatedAt,
        column.deletedAt -> deletedAt,
        column.lockVersion -> lockVersion
      )
    }.updateAndReturnGeneratedKey.apply()

    ReleaseNotification(
      id = generatedKey.toInt,
      artistAlbumId = artistAlbumId,
      notificationSentAt = notificationSentAt,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion)
  }

  def batchInsert(entities: collection.Seq[ReleaseNotification])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "artistAlbumId" -> entity.artistAlbumId,
        "notificationSentAt" -> entity.notificationSentAt,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion))
    SQL("""insert into release_notification(
      artist_album_id,
      notification_sent_at,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {artistAlbumId},
      {notificationSentAt},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: ReleaseNotification)(implicit session: DBSession): ReleaseNotification = {
    withSQL {
      update(ReleaseNotification).set(
        column.id -> entity.id,
        column.artistAlbumId -> entity.artistAlbumId,
        column.notificationSentAt -> entity.notificationSentAt,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.deletedAt -> entity.deletedAt,
        column.lockVersion -> entity.lockVersion
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ReleaseNotification)(implicit session: DBSession): Int = {
    withSQL { delete.from(ReleaseNotification).where.eq(column.id, entity.id) }.update.apply()
  }

}
