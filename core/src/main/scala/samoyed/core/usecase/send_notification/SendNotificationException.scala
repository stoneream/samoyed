package samoyed.core.usecase.send_notification

sealed abstract class SendNotificationException(message: String = null, cause: Throwable = null) extends Exception(message, cause)

object SendNotificationException {}
