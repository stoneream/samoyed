package samoyed.core.usecase.schedule_artist_album_fetch

sealed abstract class ScheduleArtistAlbumFetchException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object ScheduleArtistAlbumFetchException {}
