package samoyed.daemon.handler.create_notification
import com.google.inject.{Inject, Singleton}
import monix.eval.Task
import samoyed.core.usecase.create_notification.{CreateNotification, CreateNotificationInput}
import samoyed.daemon.handler.AbstractHandler

@Singleton
class CreateNotificationHandler @Inject() (
    config: CreateNotificationConfig,
    createNotification: CreateNotification
) extends AbstractHandler("CreateNotification", config) {
  override def execute(): Task[Unit] = Task {
    val input = CreateNotificationInput()
    createNotification.run(input)
  }
}
