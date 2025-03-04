import com.google.inject.Inject
import play.api.inject.ApplicationLifecycle
import samoyed.core.lib.db.Transaction

import scala.concurrent.Future

class DBInitializerFinalizer @Inject (
    applicationLifecycle: ApplicationLifecycle,
    transaction: Transaction
) {
  applicationLifecycle.addStopHook { () =>
    Future.successful {
      transaction.closeAll()
    }
  }
}
