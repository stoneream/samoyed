package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserFollowedArtists(
    id: Long,
    samoyedUserId: Long,
    artistId: Long,
    userFollowedArtistsImportQueueId: Long,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): UserFollowedArtists = UserFollowedArtists.save(this)(session)

  def destroy()(implicit session: DBSession): Int = UserFollowedArtists.destroy(this)(session)

}

object UserFollowedArtists extends SQLSyntaxSupport[UserFollowedArtists] {

  override val schemaName = Some("samoyed")

  override val tableName = "user_followed_artists"

  override val columns =
    Seq("id", "samoyed_user_id", "artist_id", "user_followed_artists_import_queue_id", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(ufa: SyntaxProvider[UserFollowedArtists])(rs: WrappedResultSet): UserFollowedArtists = autoConstruct(rs, ufa)
  def apply(ufa: ResultName[UserFollowedArtists])(rs: WrappedResultSet): UserFollowedArtists = autoConstruct(rs, ufa)

  val ufa = UserFollowedArtists.syntax("ufa")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[UserFollowedArtists] = {
    withSQL {
      select.from(UserFollowedArtists as ufa).where.eq(ufa.id, id)
    }.map(UserFollowedArtists(ufa.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[UserFollowedArtists] = {
    withSQL(select.from(UserFollowedArtists as ufa)).map(UserFollowedArtists(ufa.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(UserFollowedArtists as ufa)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[UserFollowedArtists] = {
    withSQL {
      select.from(UserFollowedArtists as ufa).where.append(where)
    }.map(UserFollowedArtists(ufa.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[UserFollowedArtists] = {
    withSQL {
      select.from(UserFollowedArtists as ufa).where.append(where)
    }.map(UserFollowedArtists(ufa.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(UserFollowedArtists as ufa).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      samoyedUserId: Long,
      artistId: Long,
      userFollowedArtistsImportQueueId: Long,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): UserFollowedArtists = {
    val generatedKey = withSQL {
      insert
        .into(UserFollowedArtists)
        .namedValues(
          column.samoyedUserId -> samoyedUserId,
          column.artistId -> artistId,
          column.userFollowedArtistsImportQueueId -> userFollowedArtistsImportQueueId,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    UserFollowedArtists(
      id = generatedKey,
      samoyedUserId = samoyedUserId,
      artistId = artistId,
      userFollowedArtistsImportQueueId = userFollowedArtistsImportQueueId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[UserFollowedArtists])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "samoyedUserId" -> entity.samoyedUserId,
        "artistId" -> entity.artistId,
        "userFollowedArtistsImportQueueId" -> entity.userFollowedArtistsImportQueueId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into user_followed_artists(
      samoyed_user_id,
      artist_id,
      user_followed_artists_import_queue_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {samoyedUserId},
      {artistId},
      {userFollowedArtistsImportQueueId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: UserFollowedArtists)(implicit session: DBSession): UserFollowedArtists = {
    withSQL {
      update(UserFollowedArtists)
        .set(
          column.id -> entity.id,
          column.samoyedUserId -> entity.samoyedUserId,
          column.artistId -> entity.artistId,
          column.userFollowedArtistsImportQueueId -> entity.userFollowedArtistsImportQueueId,
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

  def destroy(entity: UserFollowedArtists)(implicit session: DBSession): Int = {
    withSQL { delete.from(UserFollowedArtists).where.eq(column.id, entity.id) }.update.apply()
  }

}
