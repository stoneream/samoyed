package samoyed.daemon.handler

import monix.eval.Task
import net.logstash.logback.argument.StructuredArguments.kv
import samoyed.logging.Logger
import scala.concurrent.duration.*

abstract class AbstractHandler[T <: AbstractHandler.AbstractHandlerConfig](
    val name: String,
    val config: T
) extends Logger {

  def execute(): Task[Unit]

  def preExecute(): Task[Unit] = Task {
    info(s"Start $name")
  }

  def postExecute(): Task[Unit] = Task {
    info(s"End $name")
    info("Interval seconds ({})", kv("seconds", config.intervalSeconds))
  }

  final def start(): Task[Unit] = for {
    _ <- preExecute()
    _ <- execute()
    _ <- postExecute()
    _ <- Task.sleep(config.intervalSeconds.seconds)
    _ <- start()
  } yield ()
}

object AbstractHandler {
  abstract class AbstractHandlerConfig {
    val intervalSeconds: Long
  }
}
