/**
 * P201_明細一覧取得APIからのレスポンス
 */
export type P201GetItemsResponse = {
  // 年月（yyyy-MM）
  yearMonth : string;
  // 件数
  totalNum : number;
  // 合計支払金額
  totalAmount : number;
  // 明細リスト
  itemizedList : P201GetItemsResponseItem[] | null;
};

/**
 * P201_明細一覧取得APIからのレスポンス（明細）
 */
export type P201GetItemsResponseItem = {
  // 明細ID
  itemId : string;
  // 利用日
  date : string;
  // 明細タイトル
  title : string;
  // カテゴリID
  categoryId? : number | null;
  // メモ
  memo? : string | null;
  // 当月支払金額
  amount : number;
};

/**
 * 明細一覧画面の明細VM
 */
export type TopItemViewModel = {
  // 明細ID
  itemId : string;
  // 取引日
  date : Date;
  // 明細タイトル
  title : string;
  // 大カテゴリ
  majorCategoryId : number;
  // 小カテゴリ
  minorCategoryId? : number;
  // メモ
  memo? : string;
  // 当月支払金額
  amount : number;
};

/**
 * P201レスポンス（明細）を明細一覧VMに変換する
 * @param res 
 * @returns 明細単票
 */
function toTopItemViewModel(res: P201GetItemsResponseItem): TopItemViewModel {
  return {
    itemId : res.itemId,
    date : new Date(res.date),
    title : res.title,
    majorCategoryId : res.categoryId ?? 0, // 初期値は0（未設定）とする
    minorCategoryId : undefined,
    memo : res.memo ?? undefined,
    amount : res.amount,
  };
}

/**
 * P201レスポンス（明細）リストを明細一覧VMリストに変換する
 * @param resList 
 * @returns 明細リスト
 */
function toTopItemViewModels(resList: P201GetItemsResponseItem[]): TopItemViewModel[] {
  return resList.map(toTopItemViewModel);
}

/**
 * P201レスポンスを明細一覧VMリストに変換する
 * @param response 
 * @returns 明細リスト
 */
export function toTopItemViewModelFromApiResponse(response: P201GetItemsResponse): TopItemViewModel[] {
  if(response.itemizedList != null){
    return toTopItemViewModels(response.itemizedList);
  } else {
    return [];
  }
}

/**
 * 明細一覧ヘッダーVM
 */
export type TopHeaderViewModel = {
  // 件数
  totalNum : number;
  // 合計支払金額
  totalAmount : number;
}

/**
 * P201レスポンスを明細一覧ヘッダーVMに変換する
 * @param res 
 * @returns 
 */
export function toTopHeaderViewModel(res: P201GetItemsResponse): TopHeaderViewModel{
  return{
    totalNum : res.totalNum,
    totalAmount : res.totalAmount,
  }
}

/**
 * P201_明細一覧取得API_開発用モック
 */
export function mockGetItemsResponse(yearMonth?: string) : P201GetItemsResponse {
  if (yearMonth === "2025-11"){
    return {
      yearMonth: "2025-11",
      totalNum: 0,
      totalAmount: 0,
      itemizedList: null,
    }
  } else if (yearMonth === "2026-01"){
    return {
      yearMonth: "2026-01",
      totalNum: 2,
      totalAmount: 2000,
      itemizedList: [
        {itemId: "5", date: "2025/12/01", title: "明細タイトル11", categoryId: 1, memo: "メモ1", amount: 1000},
        {itemId: "6", date: "2025/12/02", title: "明細タイトル22", categoryId: 2, memo: "メモ2", amount: 1000},
      ]
    }
  } else if (yearMonth === "2026-02"){
    return {
      yearMonth: "2026-02",
      totalNum: 10,
      totalAmount: 10000,
      itemizedList: [
        {itemId: "11", date: "2026/01/01", title: "明細タイトル11", categoryId: 1, memo: "メモ1", amount: 1000},
        {itemId: "12", date: "2026/01/02", title: "明細タイトル12", categoryId: 2, memo: "メモ2", amount: 1000},
        {itemId: "13", date: "2026/01/03", title: "明細タイトル13", categoryId: 3, memo: "メモ1", amount: 1000},
        {itemId: "14", date: "2026/01/04", title: "明細タイトル14", categoryId: 1, memo: "メモ2", amount: 1000},
        {itemId: "15", date: "2026/01/05", title: "明細タイトル15", categoryId: 2, memo: "メモ1", amount: 1000},
        {itemId: "16", date: "2026/01/06", title: "明細タイトル16", categoryId: 3, memo: "メモ2", amount: 1000},
        {itemId: "17", date: "2026/01/07", title: "明細タイトル17", categoryId: 1, memo: "メモ1", amount: 1000},
        {itemId: "18", date: "2026/01/08", title: "明細タイトル18", categoryId: 2, memo: "メモ2", amount: 1000},
        {itemId: "19", date: "2026/01/09", title: "明細タイトル19", categoryId: 3, memo: "メモ2", amount: 1000},
        {itemId: "20", date: "2026/01/10", title: "明細タイトル20", categoryId: 4, memo: "メモ2", amount: 1000},          
      ]
    }
  } else if (yearMonth === "2026-03"){
    return {
      yearMonth: "2026-03",
      totalNum: 30,
      totalAmount: 129000000,
      itemizedList: [
        {itemId: "11", date: "2026/02/01", title: "明細タイトル21", categoryId: 1, memo: "メモ1", amount: 100000},
        {itemId: "12", date: "2026/02/02", title: "明細タイトル22", categoryId: 2, memo: "メモ2", amount: 100000},
        {itemId: "13", date: "2026/02/03", title: "明細タイトル23", categoryId: 3, memo: "メモ1", amount: 100000},
        {itemId: "14", date: "2026/02/04", title: "明細タイトル24", categoryId: 4, memo: "メモ2", amount: 100000},
        {itemId: "15", date: "2026/02/05", title: "明細タイトル25", categoryId: 5, memo: "メモ1", amount: 100000},
        {itemId: "16", date: "2026/02/06", title: "明細タイトル26", categoryId: 6, memo: "メモ2", amount: 100000},
        {itemId: "17", date: "2026/02/07", title: "明細タイトル27", categoryId: 7, memo: "メモ1", amount: 100000},
        {itemId: "18", date: "2026/02/08", title: "明細タイトル28", categoryId: 8, memo: "メモ2", amount: 100000},
        {itemId: "19", date: "2026/02/09", title: "明細タイトル29", categoryId: 9, memo: "メモ2", amount: 100000},
        {itemId: "20", date: "2026/02/10", title: "明細タイトル30", categoryId: 10, memo: "メモ2", amount: 100000},
        {itemId: "21", date: "2026/02/11", title: "明細タイトル31", categoryId: 1, memo: "メモ1", amount: 100000},
        {itemId: "22", date: "2026/02/12", title: "明細タイトル32", categoryId: 2, memo: "メモ2", amount: 100000},
        {itemId: "23", date: "2026/02/13", title: "明細タイトル33", categoryId: 3, memo: "メモ1", amount: 100000},
        {itemId: "24", date: "2026/02/14", title: "明細タイトル34", categoryId: 4, memo: "メモ2", amount: 100000},
        {itemId: "25", date: "2026/02/15", title: "明細タイトル35", categoryId: 5, memo: "メモ1", amount: 100000},
        {itemId: "26", date: "2026/02/16", title: "明細タイトル36", categoryId: 6, memo: "メモ2", amount: 100000},
        {itemId: "27", date: "2026/02/17", title: "明細タイトル37", categoryId: 7, memo: "メモ1", amount: 100000},
        {itemId: "28", date: "2026/02/18", title: "明細タイトル38", categoryId: 8, memo: "メモ2", amount: 100000},
        {itemId: "29", date: "2026/02/19", title: "明細タイトル39", categoryId: 9, memo: "メモ2", amount: 100000},
        {itemId: "30", date: "2026/02/20", title: "明細タイトル40", categoryId: 10, memo: "メモ2", amount: 100000},    
        {itemId: "31", date: "2026/02/21", title: "明細タイトル41", categoryId: 1, memo: "メモ1", amount: 100000},
        {itemId: "32", date: "2026/02/22", title: "明細タイトル42", categoryId: 2, memo: "メモ2", amount: 100000},
        {itemId: "33", date: "2026/02/23", title: "明細タイトル43", categoryId: 3, memo: "メモ1", amount: 100000},
        {itemId: "34", date: "2026/02/24", title: "明細タイトル44", categoryId: 4, memo: "メモ2", amount: 100000},
        {itemId: "35", date: "2026/02/25", title: "明細タイトル45", categoryId: 5, memo: "メモ1", amount: 100000},
        {itemId: "36", date: "2026/02/26", title: "明細タイトル46", categoryId: 6, memo: "メモ2", amount: 100000},
        {itemId: "37", date: "2026/02/27", title: "明細タイトル47", categoryId: 7, memo: "メモ1", amount: 100000},
        {itemId: "38", date: "2026/02/28", title: "明細タイトル48", categoryId: 8, memo: "メモ2", amount: 100000},
        {itemId: "39", date: "2026/02/28", title: "明細タイトル49", categoryId: 9, memo: "メモ2", amount: 100000},
        {itemId: "40", date: "2026/02/28", title: "明細タイトル50", categoryId: 10, memo: "メモ2", amount: 100000000},    
      ]
    }
  } else {
    return {
      yearMonth: "2025-12",
      totalNum: 7,
      totalAmount: 80220,
      itemizedList: [
        {itemId: "1", date: "2025/11/01", title: "湖池屋 ポテチのりしお", categoryId: 2, memo: "安定の味", amount: 120},
        {itemId: "2", date: "2025/11/02", title: "匠 麻婆豆腐", categoryId: 1, memo: "しびれる辛さが病みつき", amount: 4000},
        {itemId: "3", date: "2025/11/03", title: "クアアイナ ハンバーガー", categoryId: 1, memo: "美味しいけど高い", amount: 2000},
        {itemId: "4", date: "2025/11/04", title: "ドラゴンクエストⅠ&Ⅱ", categoryId: 3, memo: "RPGの金字塔のリメイク", amount: 7000},
        {itemId: "5", date: "2025/11/05", title: "Switch2", categoryId: 3, memo: "欲しい", amount: 60000},
        {itemId: "6", date: "2025/11/06", title: "バナナ", amount: 7000},
        {itemId: "7", date: "2025/11/07", title: "リンゴ", amount: 100},
      ]
    }
  }
}