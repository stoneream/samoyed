package samoyed.core.usecase.create_notification

sealed abstract class CreateNotificationException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object CreateNotificationException {}
