export type ValidationRuleKey  =
  | "ITEM_TITLE.REQUIRED"
  | "ITEM_TITLE.MAX_LENGTH"
  | "TOP.UPDATE_PENDING"
  | "ITEM_MEMO.MAX_LENGTH"
  | "SYSTEM_UNKOWN";

export const ERROR_MESSAGES: Record<ValidationRuleKey, string> = {
  "ITEM_TITLE.REQUIRED": "明細タイトルは必ず設定してください",
  "ITEM_TITLE.MAX_LENGTH": "明細タイトルは全角50文字以内で設定してください",
  "TOP.UPDATE_PENDING": "変更が確定していません。更新確定ボタンを押してください",
  "ITEM_MEMO.MAX_LENGTH": "メモ欄は全角100文字以内で設定してください",
  SYSTEM_UNKOWN: "不明なエラーが発生しました",
};

// キーから文言を取得するヘルパー
export function getErrorMessage(key: ValidationRuleKey): string {
  return ERROR_MESSAGES[key] ?? ERROR_MESSAGES.SYSTEM_UNKOWN;
}
