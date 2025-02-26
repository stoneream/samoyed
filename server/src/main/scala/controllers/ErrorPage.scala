package controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{RequestHeader, Result, Results}
import play.twirl.api.Html

@Singleton
class ErrorPage @Inject() (template: views.Template) extends Results {

  def badRequest[A <: RequestHeader](request: A): Result = {
    BadRequest(renderErrorPage("Bad Request", "Bad Request"))
  }

  def notFound[A <: RequestHeader](request: A): Result = {
    NotFound(renderErrorPage("Not Found", "Not Found"))
  }

  def forbidden[A <: RequestHeader](request: A): Result = {
    Forbidden(renderErrorPage("Forbidden", "Forbidden"))
  }

  def otherClientError[A <: RequestHeader](code: Int, request: A): Result = {
    Status(code)(renderErrorPage("Error", "Client Error"))
  }

  def internalServerError[A <: RequestHeader](request: A): Result = {
    InternalServerError(renderErrorPage("Internal Server Error", "Internal Server Error"))
  }

  private def renderErrorPage(pageTitle: String, message: String): Html = {
    import scalatags.Text.short.*

    val tags = div(h1(message))

    template.render(pageTitle, tags)
  }
}
