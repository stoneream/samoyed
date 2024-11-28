
CREATE TABLE artist_album_fetch_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_id INT NOT NULL COMMENT 'アーティストID',
    scheduled_at DATETIME NOT NULL COMMENT 'スケジュール日時',
    started_at DATETIME COMMENT '実行開始日時',
    finished_at DATETIME COMMENT '実行終了日時',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_id) REFERENCES artist(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アーティストアルバム取得スケジュール';

CREATE TABLE artist_album_detail_fetch_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    artist_album_id INT NOT NULL COMMENT 'アルバムID',
    scheduled_at DATETIME NOT NULL COMMENT 'スケジュール日時',
    started_at DATETIME COMMENT '実行開始日時',
    finished_at DATETIME COMMENT '実行終了日時',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン',
    FOREIGN KEY (artist_album_id) REFERENCES artist_album(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'アルバム詳細取得スケジュール';
