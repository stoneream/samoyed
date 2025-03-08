package samoyed.bot

import com.google.inject.Guice
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import samoyed.core.lib.config.ConfigModule
import samoyed.core.model.config.DiscordConfig
import samoyed.logging.Logger

object SamoyedBotMain extends Logger {

  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(
      new ConfigModule()
    )

    val discordConfig = injector.getInstance(classOf[DiscordConfig])

    val jda = JDABuilder
      .createDefault(discordConfig.botToken)
      .enableIntents(GatewayIntent.MESSAGE_CONTENT)
      .addEventListeners(injector.getInstance(classOf[ConnectSpotifyAccount]))
      .build()

    val commands = jda.updateCommands()

    commands.addCommands(
      Commands.slash(
        ConnectSpotifyAccount.slashCommandName,
        "Connect your Spotify account"
      )
    )

    commands.queue()
  }

}
