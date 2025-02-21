package views

import com.google.inject.Singleton
import play.twirl.api.Html
import scalatags.Text.short.*
import scalatags.Text.tags2.*

@Singleton
class Template {
  def render(template: Tag): Html = {
    val tags = html(
      head(
        title("Samoyed")
      ),
      body(template)
    )

    Html(tags.render)
  }

}
