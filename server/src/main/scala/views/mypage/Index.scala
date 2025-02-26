package views.mypage

import scalatags.Text.short.*
import scalatags.Text.tags2.*

object Index {
  def template(): Tag = {
    div(
      h1("My Page")
    )
  }
}
