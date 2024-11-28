package samoyed.daemon.handler.scheduled_artist_album_detail_fetch

import samoyed.daemon.handler.AbstractHandler

final case class ScheduledArtistAlbumDetailFetchConfig(
    intervalSeconds: Long = 5
) extends AbstractHandler.AbstractHandlerConfig
