package samoyed.core.usecase.scheduled_artist_album_detail_fetch

sealed abstract class ScheduledArtistAlbumDetailFetchException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object ScheduledArtistAlbumDetailFetchException {}
