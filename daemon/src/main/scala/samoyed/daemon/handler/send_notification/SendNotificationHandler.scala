package samoyed.daemon.handler.send_notification
import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.usecase.send_notification.{SendNotification, SendNotificationInput}
import samoyed.daemon.handler.AbstractHandler

@Singleton
class SendNotificationHandler @Inject() (
    config: SendNotificationConfig,
    sendNotification: SendNotification
) extends AbstractHandler("SendNotification", config) {
  override def execute(): Task[Unit] = Task {
    val input = SendNotificationInput()
    sendNotification.run(input)
  }
}
