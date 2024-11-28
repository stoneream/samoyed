package samoyed.core.lib.config

import com.google.inject.{AbstractModule, Provides, Singleton}
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto.deriveReader
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigReader, ConfigSource}
import samoyed.core.model.config.{DBsConfig, DiscordConfig, SpotifyConfig}

object ConfigModule extends AbstractModule {
  private val config = ConfigSource.default

  given [A]: ProductHint[A] = ProductHint(ConfigFieldMapping(CamelCase, CamelCase))

  @Provides
  @Singleton
  def provideDBConfig(): DBsConfig = {
    given ConfigReader[DBsConfig] = deriveReader
    config.at("db").loadOrThrow[DBsConfig]
  }

  @Provides
  @Singleton
  def provideSpotifyConfig(): SpotifyConfig = {
    given ConfigReader[SpotifyConfig] = deriveReader
    config.at("spotify").loadOrThrow[SpotifyConfig]
  }

  @Provides
  @Singleton
  def provideDiscordConfig(): DiscordConfig = {
    given ConfigReader[DiscordConfig] = deriveReader
    config.at("discord").loadOrThrow[DiscordConfig]
  }
}
