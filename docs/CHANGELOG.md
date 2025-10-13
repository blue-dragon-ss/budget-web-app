# CHANGELOG.md

# Changelog
すべての notable な変更は本ファイルに記録します。形式は Keep a Changelog 風 + SemVer。

## [v0.2.0] - 2025-10-13
### Added
- controller/service/repository 構成へ整理（#12）
- RequestIdFilter を config パッケージへ移動（#12）

### Changed
- Invoice API のレスポンスDTO導入（互換あり）（#15）

### Fixed
- NullPointerException（空の請求書ID時）（#17）

### Breaking Changes
- なし

## [v0.1.0] - 2025-10-01
### Added
- 初期Spring Boot構成（MinimalApplication、Invoice API基礎）
- PostgreSQL接続（application.yml設定）
- Docker Compose環境構築
