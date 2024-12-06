CREATE TABLE muted_label (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    label_name VARCHAR(255) NOT NULL COMMENT 'レーベル名',
    created_at DATETIME NOT NULL COMMENT '作成日時',
    updated_at DATETIME NOT NULL COMMENT '更新日時',
    deleted_at DATETIME COMMENT '削除日時',
    lock_version INT NOT NULL DEFAULT 0 COMMENT 'ロックバージョン'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '通知を除外するレーベル';
