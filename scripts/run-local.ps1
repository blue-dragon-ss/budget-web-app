# ① DBを起動（1つ上の階層の docker-compose.yml を指定）
docker compose -f ../docker-compose.yml up -d

# ② 1つ上の階層の .env を読み込んで環境変数に設定
Get-Content ../.env | ForEach-Object {
  if ($_ -match "^(?<key>[^=]+)=(?<value>.+)$") {
    [System.Environment]::SetEnvironmentVariable($matches['key'], $matches['value'])
  }
}

# ③ Gradleを実行（プロジェクトディレクトリを..に明示）
#   ※ -p .. もしくは --project-dir .. を必ず付ける
..\backend\gradlew -p ..\backend bootRun --args="--spring.profiles.active=local"
