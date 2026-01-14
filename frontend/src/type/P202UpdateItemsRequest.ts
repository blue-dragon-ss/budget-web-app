import type { P201GetItemsResponseItem, P201GetItemsResponse, TopItemViewModel } from "./P201GetItemsResponse";
import  { type YearMonth, toApiString } from "./YearMonth";

export type P202UpdateItemsRequest = {
    // 年月
    yearMonth : string;
    // 更新件数
    totalNum : number;
    // 更新リスト
    updateItemList : P202UpdateItemsRequestItem[];
};

export type P202UpdateItemsRequestItem = {
    // 明細ID
    itemId : string;
    // 利用日
    date? : Date | null;
    // 明細タイトル
    title? : string | null;
    // 利用者
    payer? : string | null;
    // 支払方法
    paymentMethod? : string | null;
    // 利用金額
    usageAmount? : number | null;
    // 支払手数料
    feeAmount? : number | null;
    // 支払総額
    totalAmount? : number | null;
    // 当月支払金額
    currentMonthPaid? : number | null;
    // 次月繰越残高
    nextMonthPaid? : number | null;
    // 新規サイン
    isNewItem? : boolean | null;
    // カテゴリID
    categoryId? : number | null;
    // メモ
    memo? : string | null;
};

// Top画面で管理する明細変更
export type TopUpdateItem = {
    // 明細ID
    itemId : string;
    // 明細タイトル
    title? : string | null;
    // カテゴリID
    categoryId? : number | null;
    // メモ
    memo? : string | null;
};

export type TopUpdatePatch = Partial<TopUpdateItem> & {itemId: string};

/**
 * P201レスポンス（明細）を初期比較用に変換する
 * @param res 
 * @returns 明細単票
 */
function toTopUpdateItem(res: P201GetItemsResponseItem): TopUpdateItem {
  return {
    itemId : res.itemId,
    title : res.title,
    categoryId : res.categoryId ?? undefined,
    memo : res.memo ?? undefined,
  };
}

/**
 * P201レスポンス（明細）リストを初期比較用のRefリストに変換する
 * @param resList 
 * @returns 明細リスト
 */
function toTopUpdateItems(resList: P201GetItemsResponseItem[]): TopUpdateItem[] {
  return resList.map(toTopUpdateItem);
}

/**
 * P201レスポンスを明細一覧VMリストに変換する
 * @param response 
 * @returns 明細リスト
 */
export function toTopUpdateItemsFromApiResponse(response: P201GetItemsResponse): TopUpdateItem[] {
  if(response.itemizedList != null){
    return toTopUpdateItems(response.itemizedList);
  } else {
    return [];
  }
}

/**
 * 明細一覧画面VMからTopUpdatePatchのリストに変換する
 * @param vmList 
 * @returns 
 */
export function toTopUpdateItemsFromTopViewModel(vmList: TopItemViewModel[]): TopUpdatePatch[] {
  return (
    vmList.map((vm) => ({
      itemId: vm.itemId,
      title: vm.title,
      categoryId: vm.majorCategoryId,
      memo: vm.memo,
    }))
  )
}

/**
 * 明細一覧画面からの更新リクエスト変換
 * @param yearMonth 
 * @param patchList 
 * @returns 
 */
export function toUpdateItemsRequestFromTop(yearMonth: YearMonth, patchList: TopUpdatePatch[]): P202UpdateItemsRequest {
  const updateList: P202UpdateItemsRequestItem[] = patchList.map((patch) => ({
    itemId: patch.itemId,
    title: patch.title ?? undefined,
    categoryId: patch.categoryId ?? undefined,
    memo: patch.memo ?? undefined,
  }));

  return {
    yearMonth: toApiString(yearMonth),
    totalNum: patchList.length,
    updateItemList: updateList,
  }
}