package actions

import actions.AbstractSessionCookieAccessor.Verified
import play.api.libs.crypto.CookieSigner
import play.api.mvc.{Cookie, DiscardingCookie, RequestHeader, Result}
import samoyed.logging.Logger

abstract class AbstractSessionCookieAccessor(val signer: CookieSigner) extends Logger {

  val cookieName: String
  val cookieSecureOption: Boolean
  val cookieHttpOnlyOption: Boolean
  val cookiePathOption: String
  val cookieMaxAge: Int

  def put(value: String)(result: Result): Result = {
    val cookie = Cookie(
      name = cookieName,
      value = sign(value),
      maxAge = Some(cookieMaxAge),
      path = cookiePathOption,
      domain = None,
      secure = cookieSecureOption,
      httpOnly = cookieHttpOnlyOption,
      sameSite = None
    )
    result.withCookies(cookie)
  }

  def delete(result: Result): Result = {
    result.discardingCookies(DiscardingCookie(cookieName))
  }

  def extract(requestHeader: RequestHeader): Option[Verified] = {
    requestHeader.cookies.get(cookieName).flatMap { cookie => verifyHmacWithExtractValue(cookie.value) }
  }

  private def verifyHmacWithExtractValue(value: String): Option[Verified] = {
    value.split(":").toList match {
      case hmac :: value :: _ =>
        if (hmac == signer.sign(value)) {
          Some(Verified(value))
        } else {
          logger.info("セッショントークンのHMACが一致しません。")
          None
        }
      case _ => None
    }
  }

  private def sign(value: String): String = s"${signer.sign(value)}:$value"

}

object AbstractSessionCookieAccessor {
  case class Verified(value: String)
}
