CREATE TABLE artist (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    name VARCHAR(255) NOT NULL COMMENT 'アーティスト名',
    spotify_artist_id VARCHAR(255) NOT NULL COMMENT 'Spotify上のアーティストID',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アーティスト';

CREATE TABLE artist_album (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_id INT NOT NULL COMMENT 'アーティストID',
    spotify_album_id VARCHAR(255) NOT NULL COMMENT 'Spotify上のアルバムID',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_id) REFERENCES artist(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム';

CREATE TABLE artist_album_detail (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_album_id INT NOT NULL COMMENT 'アルバムID',
    album_name VARCHAR(255) NOT NULL COMMENT 'アルバム名',
    release_date DATETIME NOT NULL COMMENT 'リリース日時',
    release_date_type VARCHAR(255) NOT NULL COMMENT 'リリース日時タイプ',
    label VARCHAR(255) NOT NULL COMMENT 'リリースしたレーベル',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_album_id) REFERENCES artist_album(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム詳細';

CREATE TABLE release_notification (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_album_id INT NOT NULL COMMENT 'アルバムID',
    notification_sent_at DATETIME COMMENT '通知送信日時 (未送信の場合はNULL)',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_album_id) REFERENCES artist_album(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'リリース通知';

