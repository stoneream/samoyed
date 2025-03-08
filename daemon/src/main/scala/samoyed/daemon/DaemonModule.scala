package samoyed.daemon

import com.google.inject.{AbstractModule, Provides, Singleton}
import pureconfig.*
import pureconfig.generic.ProductHint
import pureconfig.generic.scala3.HintsAwareConfigReaderDerivation.deriveReader
import pureconfig.generic.semiauto.*
import samoyed.daemon.handler.schedule_artist_album_fetch.ScheduleArtistAlbumFetchConfig
import samoyed.daemon.handler.scheduled_artist_album_detail_fetch.ScheduledArtistAlbumDetailFetchConfig
import samoyed.daemon.handler.scheduled_artist_album_fetch.ScheduledArtistAlbumFetchConfig

class DaemonModule extends AbstractModule {
  private val config = ConfigSource.default
  private def daemonConfig(daemonName: String) = config.at("samoyed.daemon").at(daemonName)

  given [A]: ProductHint[A] = ProductHint(ConfigFieldMapping(CamelCase, CamelCase))

  @Provides
  @Singleton
  def provideScheduleArtistAlbumFetchConfig: ScheduleArtistAlbumFetchConfig = {
    given ConfigReader[ScheduleArtistAlbumFetchConfig] = deriveReader
    daemonConfig("scheduleArtistAlbumFetch").loadOrThrow[ScheduleArtistAlbumFetchConfig]
  }

  @Provides
  @Singleton
  def provideScheduledArtistAlbumDetailFetchConfig: ScheduledArtistAlbumDetailFetchConfig = {
    given ConfigReader[ScheduledArtistAlbumDetailFetchConfig] = deriveReader
    daemonConfig("scheduledArtistAlbumDetailFetch").loadOrThrow[ScheduledArtistAlbumDetailFetchConfig]
  }

  @Provides
  @Singleton
  def provideScheduledArtistAlbumFetchConfig: ScheduledArtistAlbumFetchConfig = {
    given ConfigReader[ScheduledArtistAlbumFetchConfig] = deriveReader
    daemonConfig("scheduledArtistAlbumFetch").loadOrThrow[ScheduledArtistAlbumFetchConfig]
  }

}
