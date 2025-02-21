package actions

import play.api.libs.crypto.CookieSigner
import samoyed.core.model.db.SamoyedUserSession

import javax.inject.{Inject, Singleton}

@Singleton
class SamoyedUserSessionCookieAccessor @Inject() (
    cookieSigner: CookieSigner
) extends AbstractSessionCookieAccessor(cookieSigner) {
  override val cookieName: String = "sus"
  override val cookieSecureOption: Boolean = true
  override val cookieHttpOnlyOption: Boolean = true
  override val cookiePathOption: String = "/"
  override val cookieMaxAge: Int = SamoyedUserSession.EXPIRES_IN // 1 week
}
