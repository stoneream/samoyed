package samoyed.core.usecase.import_followed_artist

sealed abstract class ImportFollowedArtistException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object ImportFollowedArtistException {}
