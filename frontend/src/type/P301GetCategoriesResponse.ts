/**
 * P301_明細カテゴリ取得APIからのレスポンス
 */
export type P301GetCategoriesResponse = {
    // カテゴリリスト
    categoryList : P301GetCategoriesResponseCategory[] | null;
}

/**
 * P301_明細カテゴリ取得APIからのレスポンス（カテゴリ）
 */
export type P301GetCategoriesResponseCategory = {
    // カテゴリID
    categoryId : number;
    // カテゴリ名
    categoryName : string;
    // 上位カテゴリID
    upperCategoryId : number | null;
}

/**
 * P301_明細カテゴリ取得API_開発用モック
 */
export const mockGetCategoriesResponse: P301GetCategoriesResponse = {
  categoryList: [
    { categoryId: 0, categoryName: "未設定", upperCategoryId: null},
    { categoryId: 1, categoryName: "食事", upperCategoryId: null},
    { categoryId: 2, categoryName: "おやつ", upperCategoryId: 1},
    { categoryId: 3, categoryName: "ゲーム", upperCategoryId: 2},
  ]
}

/**
 * P301_明細カテゴリ取得API_開発用モック
 */
export const mockGetCategoriesResponseZero: P301GetCategoriesResponse = {
  categoryList : null,
}