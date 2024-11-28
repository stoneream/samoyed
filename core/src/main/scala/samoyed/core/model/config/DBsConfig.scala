package samoyed.core.model.config

case class DBsConfig(
    master: DBsConfig.DB
)

object DBsConfig {
  case class DB(
      driver: String,
      url: String,
      username: String,
      password: String,
      maximumPoolSize: Int,
      connectionTimeoutMillis: Long,
      maxLifetimeMillis: Long
  )
}
