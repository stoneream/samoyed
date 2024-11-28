package samoyed.core.usecase.scheduled_artist_album_fetch

sealed abstract class ScheduledArtistAlbumFetchException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object ScheduledArtistAlbumFetchException {}
