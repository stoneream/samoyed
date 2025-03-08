package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserSpotifyAccessToken(
    id: Long,
    samoyedUserId: Long,
    accessToken: String,
    tokenType: String,
    expiresIn: Long,
    refreshToken: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): UserSpotifyAccessToken = UserSpotifyAccessToken.save(this)(session)

  def destroy()(implicit session: DBSession): Int = UserSpotifyAccessToken.destroy(this)(session)

}

object UserSpotifyAccessToken extends SQLSyntaxSupport[UserSpotifyAccessToken] {

  override val schemaName = Some("samoyed")

  override val tableName = "user_spotify_access_tokens"

  override val columns =
    Seq("id", "samoyed_user_id", "access_token", "token_type", "expires_in", "refresh_token", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(usat: SyntaxProvider[UserSpotifyAccessToken])(rs: WrappedResultSet): UserSpotifyAccessToken = autoConstruct(rs, usat)
  def apply(usat: ResultName[UserSpotifyAccessToken])(rs: WrappedResultSet): UserSpotifyAccessToken = autoConstruct(rs, usat)

  val usat = UserSpotifyAccessToken.syntax("usat")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[UserSpotifyAccessToken] = {
    withSQL {
      select.from(UserSpotifyAccessToken as usat).where.eq(usat.id, id)
    }.map(UserSpotifyAccessToken(usat.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[UserSpotifyAccessToken] = {
    withSQL(select.from(UserSpotifyAccessToken as usat)).map(UserSpotifyAccessToken(usat.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(UserSpotifyAccessToken as usat)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[UserSpotifyAccessToken] = {
    withSQL {
      select.from(UserSpotifyAccessToken as usat).where.append(where)
    }.map(UserSpotifyAccessToken(usat.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[UserSpotifyAccessToken] = {
    withSQL {
      select.from(UserSpotifyAccessToken as usat).where.append(where)
    }.map(UserSpotifyAccessToken(usat.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(UserSpotifyAccessToken as usat).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      samoyedUserId: Long,
      accessToken: String,
      tokenType: String,
      expiresIn: Long,
      refreshToken: String,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): UserSpotifyAccessToken = {
    val generatedKey = withSQL {
      insert
        .into(UserSpotifyAccessToken)
        .namedValues(
          column.samoyedUserId -> samoyedUserId,
          column.accessToken -> accessToken,
          column.tokenType -> tokenType,
          column.expiresIn -> expiresIn,
          column.refreshToken -> refreshToken,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    UserSpotifyAccessToken(
      id = generatedKey,
      samoyedUserId = samoyedUserId,
      accessToken = accessToken,
      tokenType = tokenType,
      expiresIn = expiresIn,
      refreshToken = refreshToken,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[UserSpotifyAccessToken])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "samoyedUserId" -> entity.samoyedUserId,
        "accessToken" -> entity.accessToken,
        "tokenType" -> entity.tokenType,
        "expiresIn" -> entity.expiresIn,
        "refreshToken" -> entity.refreshToken,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into user_spotify_access_tokens(
      samoyed_user_id,
      access_token,
      token_type,
      expires_in,
      refresh_token,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {samoyedUserId},
      {accessToken},
      {tokenType},
      {expiresIn},
      {refreshToken},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: UserSpotifyAccessToken)(implicit session: DBSession): UserSpotifyAccessToken = {
    withSQL {
      update(UserSpotifyAccessToken)
        .set(
          column.id -> entity.id,
          column.samoyedUserId -> entity.samoyedUserId,
          column.accessToken -> entity.accessToken,
          column.tokenType -> entity.tokenType,
          column.expiresIn -> entity.expiresIn,
          column.refreshToken -> entity.refreshToken,
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

  def destroy(entity: UserSpotifyAccessToken)(implicit session: DBSession): Int = {
    withSQL { delete.from(UserSpotifyAccessToken).where.eq(column.id, entity.id) }.update.apply()
  }

}
