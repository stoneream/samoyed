package samoyed.core.model.db

import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import java.time.OffsetDateTime

class ArtistAlbumFetchScheduleSpec extends FixtureAnyFlatSpec with Matchers with AutoRollback {
  val aafs = ArtistAlbumFetchSchedule.syntax("aafs")

  behavior of "ArtistAlbumFetchSchedule"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ArtistAlbumFetchSchedule.find(1L)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ArtistAlbumFetchSchedule.findBy(sqls.eq(aafs.id, 1L))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ArtistAlbumFetchSchedule.findAll()
    allResults.size should be > (0)
  }
  it should "count all records" in { implicit session =>
    val count = ArtistAlbumFetchSchedule.countAll()
    count should be > (0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ArtistAlbumFetchSchedule.findAllBy(sqls.eq(aafs.id, 1L))
    results.size should be > (0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ArtistAlbumFetchSchedule.countBy(sqls.eq(aafs.id, 1L))
    count should be > (0L)
  }
  it should "create new record" in { implicit session =>
    val created = ArtistAlbumFetchSchedule.create(artistId = 1L, scheduledAt = null, createdAt = null, updatedAt = null, lockVersion = 123)
    created should not be (null)
  }
  it should "save a record" in { implicit session =>
    val entity = ArtistAlbumFetchSchedule.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ArtistAlbumFetchSchedule.save(modified)
    updated should not equal (entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ArtistAlbumFetchSchedule.findAll().head
    val deleted = ArtistAlbumFetchSchedule.destroy(entity)
    deleted should be(1)
    val shouldBeNone = ArtistAlbumFetchSchedule.find(1L)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ArtistAlbumFetchSchedule.findAll()
    entities.foreach(e => ArtistAlbumFetchSchedule.destroy(e))
    val batchInserted = ArtistAlbumFetchSchedule.batchInsert(entities)
    batchInserted.size should be > (0)
  }
}
