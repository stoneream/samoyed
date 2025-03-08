package samoyed.core.lib.spotify

import monix.eval.Task
import samoyed.logging.Logger
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException
import scala.concurrent.duration._

object SpotifyApiErrorHandler extends Logger {
  def retryTooManyRequests[A](task: Task[A], maxRetries: Int): Task[A] = {
    task.onErrorHandleWith {
      case e: TooManyRequestsException =>
        logger.error(e.getMessage, e)
        if (maxRetries > 0) {
          val retryAfter = e.getRetryAfter
          logger.info(
            "{}, {}",
            kv("retryAfter", retryAfter),
            kv("maxRetries", maxRetries)
          )
          retryTooManyRequests(task, maxRetries - 1).delayExecution(retryAfter.seconds)
        } else Task.raiseError(e)
      case e =>
        logger.error(e.getMessage, e)
        Task.raiseError(e)
    }
  }
}
