package samoyed.daemon.handler.create_notification

import samoyed.daemon.handler.AbstractHandler

final case class CreateNotificationConfig(
    intervalSeconds: Long = 30
) extends AbstractHandler.AbstractHandlerConfig
