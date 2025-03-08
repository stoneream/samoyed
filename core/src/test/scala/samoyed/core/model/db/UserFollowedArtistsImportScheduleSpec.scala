package samoyed.core.model.db

import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import java.time.OffsetDateTime

class UserFollowedArtistsImportScheduleSpec extends FixtureAnyFlatSpec with Matchers with AutoRollback {
  val ufais = UserFollowedArtistsImportSchedule.syntax("ufais")

  behavior of "UserFollowedArtistsImportSchedule"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = UserFollowedArtistsImportSchedule.find(1L)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = UserFollowedArtistsImportSchedule.findBy(sqls.eq(ufais.id, 1L))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = UserFollowedArtistsImportSchedule.findAll()
    allResults.size should be > (0)
  }
  it should "count all records" in { implicit session =>
    val count = UserFollowedArtistsImportSchedule.countAll()
    count should be > (0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = UserFollowedArtistsImportSchedule.findAllBy(sqls.eq(ufais.id, 1L))
    results.size should be > (0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = UserFollowedArtistsImportSchedule.countBy(sqls.eq(ufais.id, 1L))
    count should be > (0L)
  }
  it should "create new record" in { implicit session =>
    val created = UserFollowedArtistsImportSchedule.create(samoyedUserId = 1L, queuedAt = null, createdAt = null, updatedAt = null, lockVersion = 123)
    created should not be (null)
  }
  it should "save a record" in { implicit session =>
    val entity = UserFollowedArtistsImportSchedule.findAll().head
    // TODO modify something
    val modified = entity
    val updated = UserFollowedArtistsImportSchedule.save(modified)
    updated should not equal (entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = UserFollowedArtistsImportSchedule.findAll().head
    val deleted = UserFollowedArtistsImportSchedule.destroy(entity)
    deleted should be(1)
    val shouldBeNone = UserFollowedArtistsImportSchedule.find(1L)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = UserFollowedArtistsImportSchedule.findAll()
    entities.foreach(e => UserFollowedArtistsImportSchedule.destroy(e))
    val batchInserted = UserFollowedArtistsImportSchedule.batchInsert(entities)
    batchInserted.size should be > (0)
  }
}
