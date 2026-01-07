/**
 * 半角=1, 全角=2 として長さ（表示幅）を数える
 * ※厳密な東アジア幅を完璧に判定するものではなく、一般的な「ASCIIは半角、それ以外は全角」寄りの実装です
 */
export function countWidth(str: string): number {
  let width = 0;
  for (const ch of str) {
    const code = ch.codePointAt(0)!;

    // ASCII(0x00-0x7F)は半角扱い
    if (code <= 0x7f) {
      width += 1;
    } else {
      width += 2;
    }
  }
  return width;
}

/**
 * 対象文字列が半角幅で制限以下か判定
 * @param str 
 * @param limit 
 * @returns 
 */
export function isWithinLimit(str: string, limit: number): boolean {
  return countWidth(str) <= limit; // 半角100 or 全角50 相当
}
