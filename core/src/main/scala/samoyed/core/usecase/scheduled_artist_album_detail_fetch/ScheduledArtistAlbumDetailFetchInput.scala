package samoyed.core.usecase.scheduled_artist_album_detail_fetch

final case class ScheduledArtistAlbumDetailFetchInput(
    processCount: Int = 20 // SpotifyのAPIの制限により、一度に取得できるアルバム情報の数が20件までとなっている
)
