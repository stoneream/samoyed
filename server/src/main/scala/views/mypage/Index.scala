package views.mypage

import scalatags.Text.short.*
import scalatags.Text.tags2.*

object Index {
  case class Props(
      spotifyUserId: String
  )
  def template(
      props: Props
  ): Tag = {
    div(
      h1("My Page"),
      p("Logged in as: ", props.spotifyUserId)
    )
  }
}
