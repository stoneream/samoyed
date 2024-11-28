package samoyed.core.lib.util

import monix.eval.Task
import samoyed.logging.Logger

import scala.concurrent.duration.*

object ErrorHandler extends Logger {
  def retry[A](task: Task[A], retryMillis: Long, maxRetries: Int): Task[A] = {
    task.onErrorHandleWith { e =>
      if (maxRetries > 0) {
        error(s"Failed to execute task. Retrying in $retryMillis milliseconds.", e)
        retry(task, retryMillis, maxRetries - 1).delayExecution(retryMillis.millis)
      } else {
        Task.raiseError(e)
      }
    }
  }
}
