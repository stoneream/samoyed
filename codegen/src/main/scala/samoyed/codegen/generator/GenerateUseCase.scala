package samoyed.codegen.generator

import samoyed.codegen.model.GeneratedCode
import samoyed.codegen.util.CaseConverter

object GenerateUseCase {

  def run(
      useCaseName: String
  ): Unit = {
    render(useCaseName).foreach { GeneratedCode.write }
  }

  def render(
      useCaseName: String
  ): Seq[GeneratedCode] = {
    import CaseConverter.*

    val useCaseNameSnake = toSnakeCase(useCaseName)

    val basePath = s"./core/src/main/scala/samoyed/core/usecase/${useCaseNameSnake}"

    // language=scala
    val useCase = s"""
                     |package samoyed.core.usecase.${useCaseNameSnake}
                     |
                     |import com.google.inject.{Inject, Singleton}
                     |
                     |@Singleton
                     |class ${useCaseName} @Inject()(
                     |
                     |) {
                     |  type Input = ${useCaseName}Input
                     |  type Output = ${useCaseName}Output
                     |  type Exception = ${useCaseName}Exception
                     |
                     |
                     |  def run(input: Input) = throw new NotImplementedError("Not implemented")
                     |}
                     |
                     |""".stripMargin

    val useCasePath = s"$basePath/${useCaseName}.scala"

    // language=scala
    val useCaseInput = s"""
                          |package samoyed.core.usecase.${useCaseNameSnake}
                          |
                          |final case class ${useCaseName}Input()
                          |""".stripMargin

    val useCaseInputPath = s"$basePath/${useCaseName}Input.scala"

    // language=scala
    val useCaseOutput = s"""
                           |package samoyed.core.usecase.${useCaseNameSnake}
                           |
                           |final case class ${useCaseName}Output()
                           """.stripMargin

    val useCaseOutputPath = s"$basePath/${useCaseName}Output.scala"

    // language=scala
    val useCaseException = s"""
                              |package samoyed.core.usecase.${useCaseNameSnake}
                              |
                              |sealed abstract class ${useCaseName}Exception (message: String = null, cause: Throwable = null) extends Exception(message, cause)
                              |
                              |object ${useCaseName}Exception {
                              |}
                              |""".stripMargin

    val useCaseExceptionPath = s"$basePath/${useCaseName}Exception.scala"

    Seq(
      GeneratedCode(useCasePath, useCase), // usecase
      GeneratedCode(useCaseInputPath, useCaseInput), // usecase input
      GeneratedCode(useCaseOutputPath, useCaseOutput), // usecase output
      GeneratedCode(useCaseExceptionPath, useCaseException) // usecase exception
    )
  }

}
