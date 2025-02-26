package views

import com.google.inject.Singleton
import play.twirl.api.Html
import scalatags.Text.short.*
import scalatags.Text.tags2.*

@Singleton
class Template {
  def render(pageTitle: String, template: Tag): Html = {
    val tags = html(
      head(
        title(s"Samoyed - ${pageTitle}")
      ),
      body(template)
    )

    Html(tags.render)
  }

}
