package samoyed.core.usecase.import_followed_artist

import java.time.OffsetDateTime

final case class ImportFollowedArtistInput(accessToken: String, now: OffsetDateTime)
