package samoyed.daemon.handler.schedule_artist_album_fetch

import samoyed.daemon.handler.AbstractHandler

final case class ScheduleArtistAlbumFetchConfig(
    intervalSeconds: Long = 86400
) extends AbstractHandler.AbstractHandlerConfig
