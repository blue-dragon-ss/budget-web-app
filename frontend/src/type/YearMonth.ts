/**
 * 年月型
 */
export type YearMonth = {
  // 年
  year : number;
  // 月
  month : number;
}

/**
 * API から受け取った "yyyy-MM" をパース
 * @param response 
 * @returns 
 */
export function fromApiString(s: string): YearMonth {
  const [y, m] = s.split("-");
  return { year: Number(y), month: Number(m) };
}

// API で使う "yyyy-MM" に変換
export function toApiString(ym: YearMonth): string {
  return `${ym.year}-${ym.month.toString().padStart(2, "0")}`;
}

// 前月・次月
export function addMonths(ym: YearMonth, diff: number): YearMonth {
  let year = ym.year;
  let month = ym.month + diff;
  while (month <= 0) {
    month += 12;
    year -= 1;
  }
  while (month > 12) {
    month -= 12;
    year += 1;
  }
  return { year, month };
}