package samoyed.daemon.handler.send_notification

import samoyed.daemon.handler.AbstractHandler

final case class SendNotificationConfig(
    intervalSeconds: Long = 60
) extends AbstractHandler.AbstractHandlerConfig
