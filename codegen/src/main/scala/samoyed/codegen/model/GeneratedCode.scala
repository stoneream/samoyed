package samoyed.codegen.model

import better.files.File
import samoyed.logging.Logger

import scala.io.StdIn

case class GeneratedCode(
    path: String,
    content: String
)

object GeneratedCode extends Logger {
  def write(gc: GeneratedCode): Unit = {
    val file = File(gc.path)

    if (file.exists()) {
      logger.warn(s"${gc.path} already exists")
      logger.info("Do you want to overwrite it? [y/n]")

      if (StdIn.readLine() == "y") {
        file.writeText(gc.content)
        logger.info(s"Generated ${gc.path}")
      } else {
        logger.info(s"Skipped ${gc.path}")
      }
    } else {
      file.createFileIfNotExists(createParents = true).writeText(gc.content)
      logger.info(s"Generated ${gc.path}")
    }
  }
}
