DROP TABLE muted_label;
DROP TABLE artist_album_fetch_schedule;
DROP TABLE artist_album_detail_fetch_schedule;
DROP TABLE release_notification;
DROP TABLE artist_album_detail;
DROP TABLE artist_album;
DROP TABLE artist;


-- recreate tables
CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    name VARCHAR(255) NOT NULL COMMENT 'アーティスト名',
    spotify_artist_id VARCHAR(255) NOT NULL COMMENT 'Spotify上のアーティストID',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アーティスト';

CREATE TABLE artist_albums(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_id BIGINT NOT NULL COMMENT 'アーティストID',
    spotify_album_id VARCHAR(255) NOT NULL COMMENT 'Spotify上のアルバムID',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_id) REFERENCES artists(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム';

CREATE TABLE artist_album_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_album_id BIGINT NOT NULL COMMENT 'アルバムID',
    album_name VARCHAR(255) NOT NULL COMMENT 'アルバム名',
    release_date DATETIME NOT NULL COMMENT 'リリース日時',
    release_date_type VARCHAR(255) NOT NULL COMMENT 'リリース日時タイプ',
    label VARCHAR(255) NOT NULL COMMENT 'リリースしたレーベル',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_album_id) REFERENCES artist_albums(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム詳細';


CREATE TABLE artist_album_fetch_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_id BIGINT NOT NULL COMMENT 'アーティストID',
    scheduled_at DATETIME NOT NULL COMMENT 'スケジュール日時',
    started_at DATETIME COMMENT '実行開始日時',
    finished_at DATETIME COMMENT '実行終了日時',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_id) REFERENCES artists(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アーティストアルバム取得スケジュール';

CREATE TABLE artist_album_detail_fetch_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_album_id BIGINT NOT NULL COMMENT 'アルバムID',
    scheduled_at DATETIME NOT NULL COMMENT 'スケジュール日時',
    started_at DATETIME COMMENT '実行開始日時',
    finished_at DATETIME COMMENT '実行終了日時',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_album_id) REFERENCES artist_albums(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム詳細取得スケジュール';

-- create tables
CREATE TABLE samoyed_users(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  spotify_user_id VARCHAR(255) NOT NULL COMMENT 'SpotifyユーザーID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザー';

CREATE TABLE samoyed_sessions(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  client_id VARCHAR(255) NOT NULL COMMENT 'クライアントID',
  redirect_uri VARCHAR(255) NOT NULL COMMENT 'リダイレクトURI',
  response_type VARCHAR(255) NOT NULL COMMENT 'response_type',
  state VARCHAR(255) NOT NULL COMMENT 'state',
  code_verifier VARCHAR(255) NOT NULL COMMENT 'code_verifier',
  code_challenge_method VARCHAR(255) NOT NULL COMMENT 'code_challenge_method',
  session_token VARCHAR(255) NOT NULL COMMENT 'セッショントークン',
  expires_in BIGINT NOT NULL COMMENT '有効期限 (秒)',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'セッション';

CREATE TABLE samoyed_user_sessions(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  session_token VARCHAR(255) NOT NULL COMMENT 'セッショントークン',
  expires_in BIGINT NOT NULL COMMENT '有効期限 (秒)',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーセッション';

CREATE TABLE user_spotify_access_tokens(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  access_token VARCHAR(255) NOT NULL COMMENT 'アクセストークン',
  token_type VARCHAR(255) NOT NULL COMMENT 'トークンタイプ',
  expires_in BIGINT NOT NULL COMMENT '有効期限 (秒)',
  refresh_token VARCHAR(255) NOT NULL COMMENT 'リフレッシュトークン',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'Spotifyのユーザーのアクセストークン';

CREATE TABLE user_followed_artists_import_schedules(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  queued_at DATETIME NOT NULL COMMENT 'キューイング日時',
  started_at DATETIME COMMENT '開始日時',
  finished_at DATETIME COMMENT '終了日時',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがフォローしているアーティストのインポートキュー';

CREATE TABLE user_followed_artists(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  artist_id BIGINT NOT NULL COMMENT 'アーティストID',
  user_followed_artists_import_queue_id BIGINT NOT NULL COMMENT 'ユーザーがフォローしているアーティストのインポートキューID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがフォローしているアーティスト';

CREATE TABLE user_muted_labels(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  label_name VARCHAR(255) NOT NULL COMMENT 'レーベル名',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがミュートしているレーベル';

CREATE TABLE user_release_notifications(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT 'ユーザーID',
  artist_album_id BIGINT NOT NULL COMMENT 'アルバムID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーに対するリリース通知';
