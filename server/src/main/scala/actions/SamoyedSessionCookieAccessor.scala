package actions

import play.api.libs.crypto.CookieSigner
import samoyed.core.model.db.SamoyedSession

import javax.inject.{Inject, Singleton}

@Singleton
class SamoyedSessionCookieAccessor @Inject() (
    cookieSigner: CookieSigner
) extends AbstractSessionCookieAccessor(cookieSigner) {
  override val cookieName: String = "ss"
  override val cookieSecureOption: Boolean = true
  override val cookieHttpOnlyOption: Boolean = true
  override val cookiePathOption: String = "/"
  override val cookieMaxAge: Int = SamoyedSession.EXPIRES_IN
}
