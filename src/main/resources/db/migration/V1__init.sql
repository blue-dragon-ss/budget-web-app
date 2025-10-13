-- 請求テーブル（最小）
CREATE TABLE invoices (
  id            BIGSERIAL PRIMARY KEY,
  tenant_name   VARCHAR(100) NOT NULL,
  amount_total  NUMERIC(12,2) NOT NULL,
  bill_month    DATE NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
