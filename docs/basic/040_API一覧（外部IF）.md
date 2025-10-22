# API一覧（外部IF・①段階／POSTのみ）

本システムでは **GET／PATCH／DELETE を使用せず、すべて POST** で提供する。  
一覧検索は `/search`、単票取得は `/get`、作成は `/create`、更新は `/update`、論理削除は `/delete`、復元は `/restore` で表現する。  
ベースパスは例として **`/api/v1`** を付与している。

---

## 会員（Members）
| API名 | メソッド | パス | 主な機能 | 備考 |
|---|---|---|---|---|
| 会員登録 | POST | /api/v1/members/create | 会員を新規登録する | ULIDはサーバ生成／会員コード一意（未削除時） |
| 会員取得 | POST | /api/v1/members/get | 会員の単票を取得する | id または code を指定（どちらか） |
| 会員検索 | POST | /api/v1/members/search | 会員を条件で検索する | ページング・ソート／論理削除除外が既定 |
| 会員更新 | POST | /api/v1/members/update | 会員情報を更新する | 部分更新可（未指定は変更なし） |
| 会員削除 | POST | /api/v1/members/delete | 会員を論理削除する | deleted_at に日時を書き込む |
| 会員復元 | POST | /api/v1/members/restore | 論理削除した会員を復元する | code が未削除レコードと競合時はエラー |

---

## 請求（Invoices）
| API名 | メソッド | パス | 主な機能 | 備考 |
|---|---|---|---|---|
| 請求登録 | POST | /api/v1/invoices/create | 請求を新規登録する | 既定ステータスは ISSUED |
| 請求取得 | POST | /api/v1/invoices/get | 請求の単票を取得する | id 指定 |
| 請求検索 | POST | /api/v1/invoices/search | 請求を条件で検索する | memberId・期間・status／ページング・ソート |
| 請求更新 | POST | /api/v1/invoices/update | 請求情報を更新する | 期日・金額・件名・説明・status 等 |
| 請求削除 | POST | /api/v1/invoices/delete | 請求を論理削除する | 削除後も履歴は保持 |

---

## 入金（Payments）
| API名 | メソッド | パス | 主な機能 | 備考 |
|---|---|---|---|---|
| 入金登録 | POST | /api/v1/payments/create | 入金を新規登録する | ①段階は請求IDなしで登録可 |
| 入金取得 | POST | /api/v1/payments/get | 入金の単票を取得する | id 指定 |
| 入金検索 | POST | /api/v1/payments/search | 入金を条件で検索する | memberId・期間・method／ページング・ソート |
| 入金更新 | POST | /api/v1/payments/update | 入金情報を更新する | 日付・金額・method・refNo・備考 等 |
| 入金削除 | POST | /api/v1/payments/delete | 入金を論理削除する | 削除後も履歴は保持 |
