package actions

import actions.SamoyedSessionAction.{SessionRequest, UserSessionRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionBuilder, AnyContent, BodyParsers, Request, Result, WrappedRequest}
import samoyed.core.lib.db.Transaction
import samoyed.core.lib.db.reader.{SamoyedSessionReader, SamoyedUserSessionReader}
import samoyed.core.model.db.{SamoyedSession, SamoyedUser, SamoyedUserSession}
import scalikejdbc.DBSession

import java.time.OffsetDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SamoyedSessionAction @Inject() (
    samoyedSessionCookieAccessor: SamoyedSessionCookieAccessor,
    samoyedUserSessionCookieAccessor: SamoyedUserSessionCookieAccessor,
    transaction: Transaction,
    parser: BodyParsers.Default
)(using ExecutionContext) {

  val samoyedSession: SessionActionBuilder = new SessionActionBuilder(parser)

  val samoyedUserSession: UserSessionActionBuilder = new UserSessionActionBuilder(parser)

  class SessionActionBuilder(val parser: BodyParsers.Default) extends ActionBuilder[SessionRequest, AnyContent] {
    override protected def executionContext: ExecutionContext = summon[ExecutionContext]

    override def invokeBlock[A](request: Request[A], block: SessionRequest[A] => Future[Result]): Future[Result] = {
      // Cookieからセッショントークンを取得
      samoyedSessionCookieAccessor.extract(request) match {
        case Some(AbstractSessionCookieAccessor.Verified(sessionToken)) =>
          // セッショントークンの有効性を確認
          transaction.read { session =>
            given DBSession = session
            SamoyedSessionReader.findBySessionToken(sessionToken)
          } match {
            case Some(samoyedSession) =>
              val now = OffsetDateTime.now()
              val expiredAt = samoyedSession.createdAt.plusSeconds(samoyedSession.expiresIn)

              if (now.isAfter(expiredAt)) {
                // セッションが期限切れの場合は、セッションを削除してログインにリダイレクト
                Future.apply(samoyedSessionCookieAccessor.delete(Redirect("/login")))
              } else {
                block(SessionRequest(samoyedSession = samoyedSession, request = request))
              }
            case None =>
              // セッションが存在しない場合は、セッションを削除してログインにリダイレクト
              Future.apply(samoyedSessionCookieAccessor.delete(Redirect("/login")))
          }
        case None =>
          // セッションが存在しない・不正な場合は、セッションを削除してログインにリダイレクト
          Future.apply(samoyedSessionCookieAccessor.delete(Redirect("/login")))
      }
    }
  }

  class UserSessionActionBuilder(val parser: BodyParsers.Default) extends ActionBuilder[UserSessionRequest, AnyContent] {
    override protected def executionContext: ExecutionContext = summon[ExecutionContext]

    override def invokeBlock[A](request: Request[A], block: UserSessionRequest[A] => Future[Result]): Future[Result] = {
      // Cookieからセッショントークンを取得
      samoyedUserSessionCookieAccessor.extract(request) match {
        case Some(AbstractSessionCookieAccessor.Verified(sessionToken)) =>
          // セッショントークンの有効性を確認
          transaction.read { session =>
            given DBSession = session
            SamoyedUserSessionReader.findBySessionToken(sessionToken)
          } match {
            case Some((samoyedUserSession, samoyedUser)) =>
              val now = OffsetDateTime.now()
              val expiredAt = samoyedUserSession.createdAt.plusSeconds(samoyedUserSession.expiresIn)

              if (now.isAfter(expiredAt)) {
                // セッションが期限切れの場合は、セッションを削除してログインにリダイレクト
                Future.apply(samoyedUserSessionCookieAccessor.delete(Redirect("/login")))
              } else {
                block(UserSessionRequest(samoyedUserSession = samoyedUserSession, user = samoyedUser, request = request))
              }
            case None =>
              // セッションが存在しない場合は、セッションを削除してログインにリダイレクト
              Future.apply(samoyedUserSessionCookieAccessor.delete(Redirect("/login")))
          }
        case None =>
          // セッションが存在しない・不正な場合は、セッションを削除してログインにリダイレクト
          Future.apply(samoyedSessionCookieAccessor.delete(Redirect("/login")))
      }
    }
  }
}

object SamoyedSessionAction {
  case class SessionRequest[A](
      samoyedSession: SamoyedSession,
      request: Request[A]
  ) extends WrappedRequest[A](request)

  case class UserSessionRequest[A](
      samoyedUserSession: SamoyedUserSession,
      user: SamoyedUser,
      request: Request[A]
  ) extends WrappedRequest[A](request)
}
