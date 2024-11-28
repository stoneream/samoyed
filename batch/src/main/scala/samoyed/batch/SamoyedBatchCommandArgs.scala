package samoyed.batch

import samoyed.batch.command.import_followed_artist.ImportFollowedArtistCommandArgs

case class SamoyedBatchCommandArgs(
    commandName: String = "",
    importFollowedArtistCommandArgs: Option[ImportFollowedArtistCommandArgs] = None
)
