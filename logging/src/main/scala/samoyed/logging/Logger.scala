package samoyed.logging

import net.logstash.logback.argument.StructuredArgument
import org.slf4j.LoggerFactory

trait Logger {
  private val logger = LoggerFactory.getLogger(getClass)

  def debug(message: String): Unit = logger.debug(message)

  def debug(message: String, structuredArguments: Seq[StructuredArgument]): Unit = logger.debug(message, structuredArguments*)

  def info(message: String): Unit = logger.info(message)

  def info(message: String, structuredArguments: StructuredArgument*): Unit = logger.info(message, structuredArguments*)

  def warn(message: String): Unit = logger.warn(message)

  def warn(message: String, e: Throwable): Unit = logger.warn(message, e)

  def warn(message: String, structuredArguments: Seq[StructuredArgument]): Unit = logger.warn(message, structuredArguments*)

  def error(message: String): Unit = logger.error(message)

  def error(message: String, e: Throwable): Unit = logger.error(message, e)

  def error(message: String, structuredArguments: Seq[StructuredArgument]): Unit = logger.error(message, structuredArguments*)
}
