package samoyed.core.usecase.scheduled_import_user_followed

sealed abstract class ScheduledImportUserFollowedException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object ScheduledImportUserFollowedException {}
