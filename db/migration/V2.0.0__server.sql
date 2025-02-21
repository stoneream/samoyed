-- rename tables
ALTER TABLE artist RENAME TO artists;
ALTER TABLE artist_album RENAME TO artist_albums;
ALTER TABLE artist_album_detail RENAME TO artist_album_details;
ALTER TABLE release_notification RENAME TO release_notifications;
ALTER TABLE artist_album_fetch_schedule RENAME TO artist_album_fetch_schedules;
ALTER TABLE artist_album_detail_fetch_schedule RENAME TO artist_album_detail_fetch_schedules;

-- create tables
CREATE TABLE samoyed_users(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  spotify_user_id VARCHAR(255) NOT NULL COMMENT 'SpotifyユーザーID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザー';

CREATE TABLE samoyed_sessions(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  client_id VARCHAR(255) NOT NULL COMMENT 'クライアントID',
  redirect_uri VARCHAR(255) NOT NULL COMMENT 'リダイレクトURI',
  response_type VARCHAR(255) NOT NULL COMMENT 'response_type',
  state VARCHAR(255) NOT NULL COMMENT 'state',
  code_verifier VARCHAR(255) NOT NULL COMMENT 'code_verifier',
  code_challenge_method VARCHAR(255) NOT NULL COMMENT 'code_challenge_method',
  session_token VARCHAR(255) NOT NULL COMMENT 'セッショントークン',
  expires_in INT NOT NULL COMMENT '有効期限 (秒)',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'セッション';

CREATE TABLE samoyed_user_sessions(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  session_token VARCHAR(255) NOT NULL COMMENT 'セッショントークン',
  expires_in INT NOT NULL COMMENT '有効期限 (秒)',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーセッション';

CREATE TABLE user_spotify_access_token(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  access_token VARCHAR(255) NOT NULL COMMENT 'アクセストークン',
  token_type VARCHAR(255) NOT NULL COMMENT 'トークンタイプ',
  expires_in INT NOT NULL COMMENT '有効期限 (秒)',
  refresh_token VARCHAR(255) NOT NULL COMMENT 'リフレッシュトークン',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'Spotifyのユーザーのアクセストークン';

CREATE TABLE user_followed_artists_import_queue(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  queued_at DATETIME NOT NULL COMMENT 'キューイング日時',
  started_at DATETIME COMMENT '開始日時',
  finished_at DATETIME COMMENT '終了日時',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがフォローしているアーティストのインポートキュー';

CREATE TABLE user_followed_artists(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  artist_id INT NOT NULL COMMENT 'アーティストID',
  user_followed_artists_import_queue_id INT NOT NULL COMMENT 'ユーザーがフォローしているアーティストのインポートキューID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがフォローしているアーティスト';

CREATE TABLE user_muted_labels(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  label_name VARCHAR(255) NOT NULL COMMENT 'レーベル名',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーがミュートしているレーベル';

CREATE TABLE user_release_notification(
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  user_id INT NOT NULL COMMENT 'ユーザーID',
  artist_album_id INT NOT NULL COMMENT 'アルバムID',
  created_at DATETIME NOT NULL COMMENT '作成日時',
  updated_at DATETIME NOT NULL COMMENT '更新日時',
  deleted_at DATETIME COMMENT '削除日時',
  lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'ユーザーに対するリリース通知';
