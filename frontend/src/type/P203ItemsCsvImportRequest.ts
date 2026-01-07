import  { type YearMonth, toApiString } from "./YearMonth";

export type P203ItemsCsvImportRequest = {
    // 年月
    yearMonth : string;
    // 明細Csv
    itemFile : File;
};

/**
 * 明細一覧画面からの明細CSV読込リクエスト変換
 * @param yearMonth 
 * @param patchList 
 * @returns 
 */
export function toCsvImportRequestFromTop(yearMonth: YearMonth, file: File): P203ItemsCsvImportRequest {
  return {
    yearMonth: toApiString(yearMonth),
    itemFile: file,
  }
}