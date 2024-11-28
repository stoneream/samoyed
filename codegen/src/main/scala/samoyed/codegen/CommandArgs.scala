package samoyed.codegen

case class CommandArgs(
    useCaseName: Option[String] = None,
    commandName: Option[String] = None,
    daemonName: Option[String] = None,
    tableNameForReader: Option[String] = None,
    tableNameForWriter: Option[String] = None
)
