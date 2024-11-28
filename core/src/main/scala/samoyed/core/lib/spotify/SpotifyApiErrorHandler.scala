package samoyed.core.lib.spotify

import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.logging.Logger
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException
import scala.concurrent.duration._

object SpotifyApiErrorHandler extends Logger {
  def retryTooManyRequests[A](task: Task[A], maxRetries: Int): Task[A] = {
    task.onErrorHandleWith {
      case e: TooManyRequestsException =>
        error(e.getMessage, e)
        if (maxRetries > 0) {
          Task.raiseError(e)
        } else {
          val retryAfter = e.getRetryAfter
          info(
            "{}, {}",
            kv("retryAfter", retryAfter),
            kv("maxRetries", maxRetries)
          )
          retryTooManyRequests(task, maxRetries - 1).delayExecution(retryAfter.seconds)
        }
      case e =>
        error(e.getMessage, e)
        Task.raiseError(e)
    }
  }
}
