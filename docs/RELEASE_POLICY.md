# RELEASE_POLICY.md

## 1. バージョン規則（SemVer）
```
v<MAJOR>.<MINOR>.<PATCH>[-<PRERELEASE>][+<BUILD>]
例）v1.2.3 / v1.3.0-rc.1
```
- **MAJOR**：後方互換を壊す変更（API契約の破壊、設定互換なし等）
- **MINOR**：後方互換のある機能追加（新API、設定項目の追加等）
- **PATCH**：バグ修正・非機能変更（リファクタ無仕様変更、依存更新の軽微等）
- **PRERELEASE**：`-alpha.N` / `-beta.N` / `-rc.N` を使用  
  例）`v1.0.0-rc.1` → 安定版前の候補リリース
- **BUILD**：任意（CIビルド番号など）。対外公開では通常省略

## 2. バージョン更新基準
| 種類 | 主な変更内容 | 例 |
|------|----------------|----|
| MAJOR | 互換性破壊・API契約変更 | APIパラメータ削除、DBスキーマ変更 |
| MINOR | 新機能追加・改善 | 新エンドポイント追加、DTO導入 |
| PATCH | バグ修正・軽微変更 | NullPointer修正、設定微調整 |

## 3. ブランチ運用とタグ付け
- `main` は保護し、**PR経由でのみ**更新します。
- マージ後にタグを付与：
  ```bash
  git switch main
  git pull origin main
  git tag -a vX.Y.Z -m "リリース概要（1行）"
  git push origin vX.Y.Z
  ```
- 検証や候補版はプレリリースタグを使用（例：`v1.0.0-rc.1`）。

## 4. リリース種類の定義
| 種類 | タグ形式 | 用途 |
|------|-----------|------|
| 正式版 | `vX.Y.Z` | 安定リリース |
| プレリリース | `vX.Y.Z-rc.N` | 安定化前候補版 |
| ホットフィックス | `vX.Y.(Z+1)` | 既存版への即時修正 |

## 5. タグ削除手順
```bash
git tag -d v1.0.0             # ローカル削除
git push origin :refs/tags/v1.0.0  # リモート削除
```

## 6. CHANGELOGとの連携
- 各リリースタグ作成時に `CHANGELOG.md` を更新します。
- 変更は以下のカテゴリに分類します：
  - **Added**：新機能
  - **Changed**：変更
  - **Fixed**：修正
  - **Breaking Changes**：後方互換のない変更
  - **Removed**：削除