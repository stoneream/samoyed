package samoyed.core.model.db

import scalikejdbc._
import java.time.OffsetDateTime

case class UserMutedLabels(
    id: Long,
    samoyedUserId: Long,
    labelName: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int
) {

  def save()(implicit session: DBSession): UserMutedLabels = UserMutedLabels.save(this)(session)

  def destroy()(implicit session: DBSession): Int = UserMutedLabels.destroy(this)(session)

}

object UserMutedLabels extends SQLSyntaxSupport[UserMutedLabels] {

  override val schemaName = Some("samoyed")

  override val tableName = "user_muted_labels"

  override val columns = Seq("id", "samoyed_user_id", "label_name", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(uml: SyntaxProvider[UserMutedLabels])(rs: WrappedResultSet): UserMutedLabels = autoConstruct(rs, uml)
  def apply(uml: ResultName[UserMutedLabels])(rs: WrappedResultSet): UserMutedLabels = autoConstruct(rs, uml)

  val uml = UserMutedLabels.syntax("uml")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession): Option[UserMutedLabels] = {
    withSQL {
      select.from(UserMutedLabels as uml).where.eq(uml.id, id)
    }.map(UserMutedLabels(uml.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[UserMutedLabels] = {
    withSQL(select.from(UserMutedLabels as uml)).map(UserMutedLabels(uml.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(UserMutedLabels as uml)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[UserMutedLabels] = {
    withSQL {
      select.from(UserMutedLabels as uml).where.append(where)
    }.map(UserMutedLabels(uml.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[UserMutedLabels] = {
    withSQL {
      select.from(UserMutedLabels as uml).where.append(where)
    }.map(UserMutedLabels(uml.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(UserMutedLabels as uml).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      samoyedUserId: Long,
      labelName: String,
      createdAt: OffsetDateTime,
      updatedAt: OffsetDateTime,
      deletedAt: Option[OffsetDateTime] = None,
      lockVersion: Int
  )(implicit session: DBSession): UserMutedLabels = {
    val generatedKey = withSQL {
      insert
        .into(UserMutedLabels)
        .namedValues(
          column.samoyedUserId -> samoyedUserId,
          column.labelName -> labelName,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt,
          column.deletedAt -> deletedAt,
          column.lockVersion -> lockVersion
        )
    }.updateAndReturnGeneratedKey.apply()

    UserMutedLabels(
      id = generatedKey,
      samoyedUserId = samoyedUserId,
      labelName = labelName,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion
    )
  }

  def batchInsert(entities: collection.Seq[UserMutedLabels])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "samoyedUserId" -> entity.samoyedUserId,
        "labelName" -> entity.labelName,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion
      )
    )
    SQL("""insert into user_muted_labels(
      samoyed_user_id,
      label_name,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {samoyedUserId},
      {labelName},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: UserMutedLabels)(implicit session: DBSession): UserMutedLabels = {
    withSQL {
      update(UserMutedLabels)
        .set(
          column.id -> entity.id,
          column.samoyedUserId -> entity.samoyedUserId,
          column.labelName -> entity.labelName,
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

  def destroy(entity: UserMutedLabels)(implicit session: DBSession): Int = {
    withSQL { delete.from(UserMutedLabels).where.eq(column.id, entity.id) }.update.apply()
  }

}
