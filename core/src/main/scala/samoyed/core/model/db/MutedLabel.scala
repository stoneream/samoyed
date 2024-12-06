package samoyed.core.model.db

import scalikejdbc.{autoConstruct, ResultName, SQLSyntaxSupport, WrappedResultSet}

import java.time.OffsetDateTime

case class MutedLabel(
    id: Int,
    labelName: String,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime,
    deletedAt: Option[OffsetDateTime],
    lockVersion: Int
)

object MutedLabel extends SQLSyntaxSupport[MutedLabel] {
  def apply(rn: ResultName[MutedLabel])(rs: WrappedResultSet): MutedLabel = autoConstruct(rs, rn)
}
