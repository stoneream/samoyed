package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class SamoyedUser(
    id: Long,
    spotifyUserId: String,
    discordUserId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): SamoyedUser = SamoyedUser.save(this)(session)

  def destroy()(implicit session: DBSession): Int = SamoyedUser.destroy(this)(session)

}

object SamoyedUser extends SQLSyntaxSupport[SamoyedUser] {

  override val schemaName = Some("samoyed")

  override val tableName = "samoyed_users"

  override val columns = Seq("id", "spotify_user_id", "discord_user_id", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(su: SyntaxProvider[SamoyedUser])(rs: WrappedResultSet): SamoyedUser = autoConstruct(rs, su)
  def apply(su: ResultName[SamoyedUser])(rs: WrappedResultSet): SamoyedUser = autoConstruct(rs, su)

  val su = SamoyedUser.syntax("su")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[SamoyedUser] = {
    withSQL {
      select.from(SamoyedUser as su).where.eq(su.id, id)
    }.map(SamoyedUser(su.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[SamoyedUser] = {
    withSQL(select.from(SamoyedUser as su)).map(SamoyedUser(su.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(SamoyedUser as su)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[SamoyedUser] = {
    withSQL {
      select.from(SamoyedUser as su).where.append(where)
    }.map(SamoyedUser(su.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[SamoyedUser] = {
    withSQL {
      select.from(SamoyedUser as su).where.append(where)
    }.map(SamoyedUser(su.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(SamoyedUser as su).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      spotifyUserId: String,
      discordUserId: String,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): SamoyedUser = {
    val generatedKey = withSQL {
      insert
        .into(SamoyedUser)
        .namedValues(
          column.spotifyUserId -> spotifyUserId,
          column.discordUserId -> discordUserId,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    SamoyedUser(
      id = generatedKey,
      spotifyUserId = spotifyUserId,
      discordUserId = discordUserId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[SamoyedUser])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "spotifyUserId" -> entity.spotifyUserId,
        "discordUserId" -> entity.discordUserId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into samoyed_users(
      spotify_user_id,
      discord_user_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {spotifyUserId},
      {discordUserId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: SamoyedUser)(implicit session: DBSession): SamoyedUser = {
    withSQL {
      update(SamoyedUser)
        .set(
          column.id -> entity.id,
          column.spotifyUserId -> entity.spotifyUserId,
          column.discordUserId -> entity.discordUserId,
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

  def destroy(entity: SamoyedUser)(implicit session: DBSession): Int = {
    withSQL { delete.from(SamoyedUser).where.eq(column.id, entity.id) }.update.apply()
  }

}
