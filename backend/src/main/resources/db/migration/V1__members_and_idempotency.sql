-- members テーブル
CREATE TABLE IF NOT EXISTS members (
  id           VARCHAR(26)      PRIMARY KEY,       -- ULID(26)
  code         VARCHAR(50)   NOT NULL,
  name         VARCHAR(200)  NOT NULL,
  email        VARCHAR(320),
  note         TEXT,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at   TIMESTAMPTZ
);

-- 論理削除前提の一意制（deleted_at IS NULL のみ対象）: Partial Unique Index（PostgreSQL）
CREATE UNIQUE INDEX IF NOT EXISTS uq_members_code_active
  ON members (code)
  WHERE deleted_at IS NULL;

-- 更新日時トリガ（任意：アプリ側で更新するなら不要）
-- ここではアプリ側で @PreUpdate する想定のため省略

-- Idempotency保存テーブル（create系で任意使用）
CREATE TABLE IF NOT EXISTS idempotent_requests (
  id                BIGSERIAL PRIMARY KEY,
  idempotency_key   VARCHAR(200) NOT NULL,
  endpoint          VARCHAR(200) NOT NULL,
  request_hash      VARCHAR(64)     NOT NULL, -- SHA-256 hex
  member_id         VARCHAR(26),              -- 作成リソースID（今回はmembers.id）
  response_body     JSONB,                 -- 直前レスポンスのキャッシュ（任意）
  created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_idem_endpoint_key
  ON idempotent_requests (endpoint, idempotency_key);
