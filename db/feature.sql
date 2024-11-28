create table user(
  id int auto_increment primary key,
  created_at datetime not null comment '作成日時',
  updated_at datetime not null comment '更新日時',
  deleted_at datetime comment '削除日時'
) engine = InnoDB default charset = utf8mb4 comment 'ユーザー';

create table spotify_user(
  id int auto_increment primary key,
  user_id int not null,
  spotify_id varchar(255) not null comment 'SpotifyのユーザID',
  created_at datetime not null comment '作成日時',
  updated_at datetime not null comment '更新日時',
  deleted_at datetime comment '削除日時',
  foreign key (user_id) references user(id)
) engine = InnoDB default charset = utf8mb4 comment 'ユーザーに紐づくSpotifyのユーザー情報';

create table spotify_user_token(
  id int auto_increment primary key,
  user_id int not null,
  access_token varchar(255) not null,
  refresh_token varchar(255) not null,
  expires_in int not null,
  created_at datetime not null comment '作成日時',
  updated_at datetime not null comment '更新日時',
  deleted_at datetime comment '削除日時',
  foreign key (user_id) references user(id)
) engine = InnoDB default charset = utf8mb4 comment 'Spotifyのユーザーのアクセストークン';

create table followed_artist(
  id int auto_increment primary key,
  user_id int not null,
  artist_id int not null,
  created_at datetime not null comment '作成日時',
  updated_at datetime not null comment '更新日時',
  deleted_at datetime comment '削除日時',
  foreign key (user_id) references user(id),
  foreign key (artist_id) references artist(id)
) engine = InnoDB default charset = utf8mb4 comment 'ユーザーがフォローしているアーティスト';

create table release_notification_user_mapping(
  id int auto_increment primary key,
  release_notification_id int not null,
  user_id int not null,
  notified_at datetime comment '通知日時 (nullの場合は未通知)',
  created_at datetime not null comment '作成日時',
  updated_at datetime not null comment '更新日時',
  deleted_at datetime comment '削除日時',
  foreign key (release_notification_id) references release_notification(id),
  foreign key (user_id) references user(id)
) engine = InnoDB default charset = utf8mb4 comment 'リリース通知とユーザーのマッピング';
