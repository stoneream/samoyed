# SAMOYED

Spotify 新着リリースを巡回 & 通知くん

## インストール方法

### required

- sbt
- Docker
- MariaDB

### DBのマイグレーション

```bash
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="samoyed"
DB_USER="root"
DB_PASSWORD="password"

db/migrate.sh $DB_HOST $DB_PORT $DB_NAME $DB_USER $DB_PASSWORD
```

### 設定項目

`src/main/resources/reference.conf` を参照。

## Development

### 環境の構築

DBの立ち上げとFlywayによるマイグレーションが行われる。

```bash
docker compose up -d
```

### UseCaseの生成

UseCaseのひな形を生成する。

```bash
sbt "codegen/runMain samoyed.codegen.SamoyedCodeGenMain --usecase-name UseCaseName"
```

### Commandの生成

Commandのひな形を生成する。

```bash
sbt "codegen/runMain samoyed.codegen.SamoyedCodeGenMain --command-name CommandName"
```

### Daemonの生成

Daemonのひな形を生成する。

```bash
sbt "codegen/runMain samoyed.codegen.SamoyedCodeGenMain --daemon-name ScheduleArtistAlbumFetch"
```

## 起動方法

### アクセストークンの取得方法

[oauth2c](https://github.com/cloudentity/oauth2c) での取得例を以下に示す。

```bash
CLIENT_ID=here
CLIENT_SECRET=here

oauth2c https://accounts.spotify.com \
  --client-id $CLIENT_ID \
  --client-secret $CLIENT_SECRET \
  --redirect-url http://localhost:8080/callback \
  --response-types code \
  --response-mode query \
  --scopes user-follow-read \
  --grant-type authorization_code \
  --auth-method client_secret_basic
```

### バッチ

```bash
ACCESS_TOKEN=here

# フォロー中アーティストを巡回対象のアーティストとして取り込む
sbt "batch/runMain samoyed.batch.SamoyedBatchMain import-followed-artist --access-token $ACCESS_TOKEN"
```

### デーモン

```bash
# デーモンでは以下の処理を行う
# ・ 巡回スケジュールを作成
# ・ アーティストの新着リリースを巡回
# ・ 新着リリースの詳細情報を取得
# ・ 通知作成
# ・ 通知送信

sbt "daemon/runMain samoyed.daemon.SamoyedDaemonMain"
```
