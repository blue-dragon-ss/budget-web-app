import { mockGetItemsResponse, type P201GetItemsResponse } from "../type/P201GetItemsResponse.ts";
import type { P202UpdateItemsRequest } from "../type/P202UpdateItemsRequest.ts";
import { mockUpdateItemsResponse, type P202UpdateItemsResponse } from "../type/P202UpdateItemsResponse.ts";
import type { P203ItemsCsvImportRequest } from "../type/P203ItemsCsvImportRequest.ts";
import { mockGetCategoriesResponse, mockGetCategoriesResponseZero, type P301GetCategoriesResponse } from "../type/P301GetCategoriesResponse.ts";
import { type P203ItemsCsvImportResponse, mockCsvImportResponse } from "../type/P203ItemsCsvImportResponse.ts"

// 開発用モック
const USE_MOCK_GET_ITEMS_API = true;
const ERROR_MOCK_GET_ITEMS_API = true;

const USE_MOCK_UPDATE_API = true;
const ERROR_MOCK_UPDATE_API = true;

const USE_MOCK_CATEGORY_API = true;
const USE_MOCK_CATEGORY_ZERO_API = false;
const ERROR_MOCK_CATEGORY_API = true;

const USE_MOCK_CSV_IMPORT_API = true;
const ERROR_MOCK_CSV_IMPORT_API = true;

export async function getMembers() {
  const res = await fetch("/api/members");
  if (!res.ok) {
    throw new Error("メンバー取得に失敗しました");
  }
  return await res.json();
}

/**
 * P201_明細一覧取得API
 * @param yearMonth 
 * @returns 
 */
export async function getItems(yearMonth? : string) {
  // 開発用レスポンス
  if(USE_MOCK_GET_ITEMS_API) {
    return mockGetItemsResponse(yearMonth);
  } else if (ERROR_MOCK_GET_ITEMS_API) {
      throw new Error("明細の一覧取得に失敗しました。ページを更新してください");
  }

  const apiPath = yearMonth 
    ? `/api/v1/items?yearMonth=${encodeURIComponent(yearMonth)}`
    : "/api/v1/items";

  const res = await fetch(apiPath, {
    method: "GET",
    headers: {
      Accept: "application/json",
      // 認証トークンなどがあればここに
      // Authorization: `Bearer ${token}`,
    }
  });
  if (!res.ok) {
    throw new Error("明細の一覧取得に失敗しました。ページを更新してください");
  }
  const json = (await res.json()) as P201GetItemsResponse;
  return json;
}

/**
 * P202_明細更新API
 * @param request 
 * @returns 
 */
export async function updateItems(request: P202UpdateItemsRequest) {
  if(USE_MOCK_UPDATE_API){
    return mockUpdateItemsResponse(request.yearMonth);
  } else if (ERROR_MOCK_UPDATE_API) {
      throw new Error("明細更新に失敗しました");
  }

  const res = await fetch("/api/v1/items/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("明細更新に失敗しました");
  }
  const json = (await res.json()) as P202UpdateItemsResponse;
  return json;
}

/**
 * P203_明細CSV読込API
 * @param request 
 * @returns 
 */
export async function importItemsfromCsv(request: P203ItemsCsvImportRequest) {
  if(USE_MOCK_CSV_IMPORT_API) {
    return mockCsvImportResponse(request.yearMonth);
  } else if (ERROR_MOCK_CSV_IMPORT_API) {
      throw new Error("読込に失敗しました。時間を空けてもう一度試してください");
  }

  const res = await fetch("/api/v1/items/import/csv", {
    method: "POST",
    headers: {
      "Content-Type": "multipart/form-data",
      Accept: "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("読込に失敗しました。時間を空けてもう一度試してください");
  }
  const json = (await res.json()) as P203ItemsCsvImportResponse;
  return json;
}

/**
 * P301_明細カテゴリ取得API
 * @returns 
 */
export async function getCategories() {
  // 開発用レスポンス
  if(USE_MOCK_CATEGORY_API) {
    return mockGetCategoriesResponse;
  } else if(USE_MOCK_CATEGORY_ZERO_API) {
    return mockGetCategoriesResponseZero;
  } else if (ERROR_MOCK_CATEGORY_API) {
      throw new Error("明細カテゴリ取得に失敗しました。ページを更新してください");
  }

  const res = await fetch("/api/v1/categories", {
    method: "GET",
    headers: {
      Accept: "application/json",
      // 認証トークンなどがあればここに
      // Authorization: `Bearer ${token}`,
    },
  });
  if (!res.ok) {
    throw new Error("明細カテゴリ取得に失敗しました。ページを更新してください");
  }
  const json = (await res.json()) as P301GetCategoriesResponse;
  return await json;
}