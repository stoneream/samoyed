package samoyed.daemon.handler.scheduled_artist_album_fetch

import samoyed.daemon.handler.AbstractHandler

final case class ScheduledArtistAlbumFetchConfig(
    intervalSeconds: Long = 15
) extends AbstractHandler.AbstractHandlerConfig
