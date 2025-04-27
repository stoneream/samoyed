package database

import scalikejdbc._
import java.time.{OffsetDateTime}

case class MutedLabel(
  id: Int,
  labelName: String,
  createdAt: OffsetDateTime,
  updatedAt: OffsetDateTime,
  deletedAt: Option[OffsetDateTime] = None,
  lockVersion: Int) {

  def save()(implicit session: DBSession): MutedLabel = MutedLabel.save(this)(session)

  def destroy()(implicit session: DBSession): Int = MutedLabel.destroy(this)(session)

}


object MutedLabel extends SQLSyntaxSupport[MutedLabel] {

  override val schemaName = Some("dev_samoyed")

  override val tableName = "muted_label"

  override val columns = Seq("id", "label_name", "created_at", "updated_at", "deleted_at", "lock_version")

  def apply(ml: SyntaxProvider[MutedLabel])(rs: WrappedResultSet): MutedLabel = autoConstruct(rs, ml)
  def apply(ml: ResultName[MutedLabel])(rs: WrappedResultSet): MutedLabel = autoConstruct(rs, ml)

  val ml = MutedLabel.syntax("ml")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[MutedLabel] = {
    withSQL {
      select.from(MutedLabel as ml).where.eq(ml.id, id)
    }.map(MutedLabel(ml.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[MutedLabel] = {
    withSQL(select.from(MutedLabel as ml)).map(MutedLabel(ml.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(MutedLabel as ml)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[MutedLabel] = {
    withSQL {
      select.from(MutedLabel as ml).where.append(where)
    }.map(MutedLabel(ml.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[MutedLabel] = {
    withSQL {
      select.from(MutedLabel as ml).where.append(where)
    }.map(MutedLabel(ml.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(MutedLabel as ml).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    labelName: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime] = None,
    lockVersion: Int)(implicit session: DBSession): MutedLabel = {
    val generatedKey = withSQL {
      insert.into(MutedLabel).namedValues(
        column.labelName -> labelName,
        column.createdAt -> createdAt,
        column.updatedAt -> updatedAt,
        column.deletedAt -> deletedAt,
        column.lockVersion -> lockVersion
      )
    }.updateAndReturnGeneratedKey.apply()

    MutedLabel(
      id = generatedKey.toInt,
      labelName = labelName,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      lockVersion = lockVersion)
  }

  def batchInsert(entities: collection.Seq[MutedLabel])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "labelName" -> entity.labelName,
        "createdAt" -> entity.createdAt,
        "updatedAt" -> entity.updatedAt,
        "deletedAt" -> entity.deletedAt,
        "lockVersion" -> entity.lockVersion))
    SQL("""insert into muted_label(
      label_name,
      created_at,
      updated_at,
      deleted_at,
      lock_version
    ) values (
      {labelName},
      {createdAt},
      {updatedAt},
      {deletedAt},
      {lockVersion}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: MutedLabel)(implicit session: DBSession): MutedLabel = {
    withSQL {
      update(MutedLabel).set(
        column.id -> entity.id,
        column.labelName -> entity.labelName,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.deletedAt -> entity.deletedAt,
        column.lockVersion -> entity.lockVersion
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: MutedLabel)(implicit session: DBSession): Int = {
    withSQL { delete.from(MutedLabel).where.eq(column.id, entity.id) }.update.apply()
  }

}
