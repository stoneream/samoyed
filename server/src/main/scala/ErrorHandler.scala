import com.google.inject.{Inject, Singleton}
import controllers.ErrorPage
import org.apache.pekko.http.scaladsl.model.{StatusCode, StatusCodes}
import play.api.http.HttpErrorHandler
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

// https://www.playframework.com/documentation/3.0.x/ScalaErrorHandling#Supplying-a-custom-error-handler

@Singleton
class ErrorHandler @Inject() (
    errorPage: ErrorPage
) extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val result = statusCode match {
      case 400 => errorPage.badRequest(request)
      case 403 => errorPage.forbidden(request)
      case 404 => errorPage.notFound(request)
      case code if code >= 400 && code < 500 => errorPage.otherClientError(code, request)
      case code =>
        throw new IllegalArgumentException(
          s"non client error [statusCode=$code]"
        )
    }
    Future.successful(result)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful {
      errorPage.internalServerError(request)
    }
  }
}
