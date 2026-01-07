/**
 * P202_明細更新APIからのレスポンス
 */
export type P202UpdateItemsResponse = {
    // 年月（yyyy-MM）
    yearMonth : string;
    // 件数
    totalNum : number;
    // 更新リスト
    updateResultList : P202UpdateItemsResponseUpdateResult[]
}

/**
 * P202_明細更新APIからのレスポンス（更新結果）
 */
type P202UpdateItemsResponseUpdateResult = {
    // 明細ID（ULDI）
    itemId : string;
    // 結果
    status : boolean;
    // メッセージ
    message? : string | null;
}

/**
 * P202_明細更新API_開発用モック
 */
export function mockUpdateItemsResponse(yearMonth?: string) : P202UpdateItemsResponse {
    if (yearMonth === "2026-01"){
        return {
            yearMonth: "2026-01",
            totalNum: 4,
            updateResultList: [
                {itemId: "5", status: false, message: "エラーです"},
                {itemId: "6", status: false, message: "エラーです"},
            ]
        }
    } else if (yearMonth === "2026-02"){
        return {
            yearMonth: "2026-02",
            totalNum: 1,
            updateResultList: [
                {itemId: "20", status: true },
            ]
        }
    } else {
        return {
            yearMonth: "2025-12",
            totalNum: 2,
            updateResultList: [
                {itemId: "6", status: true },
                {itemId: "7", status: true },
            ]
        }
    }
}