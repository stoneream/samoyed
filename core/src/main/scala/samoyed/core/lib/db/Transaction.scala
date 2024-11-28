package samoyed.core.lib.db

import com.google.inject.{Inject, Singleton}
import com.zaxxer.hikari.HikariDataSource
import monix.catnap.Semaphore
import monix.eval.Task
import monix.execution.Scheduler.Implicits.traced
import samoyed.core.model.config.DBsConfig
import samoyed.logging.Logger
import scalikejdbc.*

@Singleton
class Transaction @Inject() (
    dbsConfig: DBsConfig
) extends Logger {
  private final val master: Symbol = Symbol("master")

  private val masterConfig = dbsConfig.master
  private val masterDs = new HikariDataSource()
  masterDs.setDriverClassName(masterConfig.driver)
  masterDs.setJdbcUrl(masterConfig.url)
  masterDs.setUsername(masterConfig.username)
  masterDs.setPassword(masterConfig.password)
  masterDs.setMaximumPoolSize(masterConfig.maximumPoolSize)
  masterDs.setMaxLifetime(masterConfig.maxLifetimeMillis)
  masterDs.setConnectionTimeout(masterConfig.connectionTimeoutMillis)

  private val masterCp = new DataSourceConnectionPool(masterDs)

  ConnectionPool.add(master, masterCp)
  ConnectionPool.singleton(masterCp)

  // データベースのコネクション数以上に同時に処理されないようにする
  private val semaphore = Semaphore[Task](masterConfig.maximumPoolSize).runSyncUnsafe()

  def closeAll(): Unit = {
    masterDs.close()
  }

  final def read[A](f: DBSession => A): Task[A] = {
    semaphore.withPermit {
      Task {
        NamedDB(master).localTx(f)
      }
    }
  }

  final def write[A](f: DBSession => A): Task[A] = {
    semaphore.withPermit {
      Task {
        NamedDB(master).localTx(f)
      }
    }
  }

  final def readForWrite[A](f: DBSession => A): Task[A] = {
    semaphore.withPermit {
      Task {
        NamedDB(master).localTx(f)
      }
    }
  }
}
