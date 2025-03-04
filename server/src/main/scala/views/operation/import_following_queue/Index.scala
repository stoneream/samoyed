package views.operation.import_following_queue

import scalatags.Text.attrs.*
import scalatags.Text.short.*
import scalatags.Text.tags2.*
import view_utils.Messages

object Index {
  case class Props(
      csrfToken: String,
      messagesOpt: Option[Messages]
  )

  def template(props: Props): Tag = {
    div(
      h1("フォロー中アーティストのインポート"),
      renderMessages(props.messagesOpt),
      form(
        action := "/operation/import-following-queue",
        method := "post",
        div(
          input(
            `type` := "hidden",
            name := "csrfToken",
            value := props.csrfToken
          ),
          button(
            tpe := "submit",
            "Import"
          )
        )
      )
    )
  }

  private def renderMessages(messagesOpt: Option[Messages]): Tag = {
    messagesOpt match
      case Some(messages) =>
        div(
          cls := "message",
          messages.items.map { item =>
            p(item.text)
          }
        )
      case None => div(cls := "message")
  }
}
