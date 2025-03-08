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

### 環境変数

tbd

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
