package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class SamoyedSession(
    id: Long,
    clientId: String,
    redirectUri: String,
    responseType: String,
    state: String,
    codeVerifier: String,
    codeChallengeMethod: String,
    expiresIn: Long,
    discordUserId: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): SamoyedSession = SamoyedSession.save(this)(session)

  def destroy()(implicit session: DBSession): Int = SamoyedSession.destroy(this)(session)

}

object SamoyedSession extends SQLSyntaxSupport[SamoyedSession] {

  override val schemaName = Some("samoyed")

  override val tableName = "samoyed_sessions"

  override val columns = Seq(
    "id",
    "client_id",
    "redirect_uri",
    "response_type",
    "state",
    "code_verifier",
    "code_challenge_method",
    "expires_in",
    "discord_user_id",
    "created_at",
    "updated_at",
    "deleted_at",
    "lock_version"
  )

  def apply(ss: SyntaxProvider[SamoyedSession])(rs: WrappedResultSet): SamoyedSession = autoConstruct(rs, ss)
  def apply(ss: ResultName[SamoyedSession])(rs: WrappedResultSet): SamoyedSession = autoConstruct(rs, ss)

  val ss = SamoyedSession.syntax("ss")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[SamoyedSession] = {
    withSQL {
      select.from(SamoyedSession as ss).where.eq(ss.id, id)
    }.map(SamoyedSession(ss.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[SamoyedSession] = {
    withSQL(select.from(SamoyedSession as ss)).map(SamoyedSession(ss.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(SamoyedSession as ss)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[SamoyedSession] = {
    withSQL {
      select.from(SamoyedSession as ss).where.append(where)
    }.map(SamoyedSession(ss.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[SamoyedSession] = {
    withSQL {
      select.from(SamoyedSession as ss).where.append(where)
    }.map(SamoyedSession(ss.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(SamoyedSession as ss).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      clientId: String,
      redirectUri: String,
      responseType: String,
      state: String,
      codeVerifier: String,
      codeChallengeMethod: String,
      expiresIn: Long,
      discordUserId: String,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): SamoyedSession = {
    val generatedKey = withSQL {
      insert
        .into(SamoyedSession)
        .namedValues(
          column.clientId -> clientId,
          column.redirectUri -> redirectUri,
          column.responseType -> responseType,
          column.state -> state,
          column.codeVerifier -> codeVerifier,
          column.codeChallengeMethod -> codeChallengeMethod,
          column.expiresIn -> expiresIn,
          column.discordUserId -> discordUserId,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    SamoyedSession(
      id = generatedKey,
      clientId = clientId,
      redirectUri = redirectUri,
      responseType = responseType,
      state = state,
      codeVerifier = codeVerifier,
      codeChallengeMethod = codeChallengeMethod,
      expiresIn = expiresIn,
      discordUserId = discordUserId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[SamoyedSession])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "clientId" -> entity.clientId,
        "redirectUri" -> entity.redirectUri,
        "responseType" -> entity.responseType,
        "state" -> entity.state,
        "codeVerifier" -> entity.codeVerifier,
        "codeChallengeMethod" -> entity.codeChallengeMethod,
        "expiresIn" -> entity.expiresIn,
        "discordUserId" -> entity.discordUserId,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into samoyed_sessions(
      client_id,
      redirect_uri,
      response_type,
      state,
      code_verifier,
      code_challenge_method,
      expires_in,
      discord_user_id,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {clientId},
      {redirectUri},
      {responseType},
      {state},
      {codeVerifier},
      {codeChallengeMethod},
      {expiresIn},
      {discordUserId},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: SamoyedSession)(implicit session: DBSession): SamoyedSession = {
    withSQL {
      update(SamoyedSession)
        .set(
          column.id -> entity.id,
          column.clientId -> entity.clientId,
          column.redirectUri -> entity.redirectUri,
          column.responseType -> entity.responseType,
          column.state -> entity.state,
          column.codeVerifier -> entity.codeVerifier,
          column.codeChallengeMethod -> entity.codeChallengeMethod,
          column.expiresIn -> entity.expiresIn,
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

  def destroy(entity: SamoyedSession)(implicit session: DBSession): Int = {
    withSQL { delete.from(SamoyedSession).where.eq(column.id, entity.id) }.update.apply()
  }

}
