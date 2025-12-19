/**
 * P203_明細CSV読込APIからのレスポンス
 */
export type P203ItemsCsvImportResponse = {
    // 読込件数
    total : number;
    // 成功件数
    success : number;
    // 失敗件数
    failed: number;
    // エラーリスト
    errors? : P203ItemsCsvImportResponseErrors[] | null;
}

/**
 * P203_明細CSV読込APIからのレスポンス（エラーリスト）
 */
type P203ItemsCsvImportResponseErrors = {
    // 明細ID（ULDI）
    line : number;
    // 結果
    code : string;
    // メッセージ
    message? : string;
}

/**
 * P203_明細CSV読込API_開発用モック
 */
export function mockCsvImportResponse(yearMonth?: string) : P203ItemsCsvImportResponse {
    if (yearMonth === "2025-12"){
        return {
            total: 4,
            success: 4,
            failed: 0,
        }
    } else {
        return {
            total: 2,
            success: 0,
            failed: 2,
            errors: [
                {line: 1, code: "code1", message: "エラー1です" },
                {line: 2, code: "code2", message: "エラー2です" },
            ]
        }
    }
}