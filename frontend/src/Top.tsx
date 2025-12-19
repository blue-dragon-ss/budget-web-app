import { useCallback, useEffect, useState } from "react"; // 追加
import "./Top.css";
import TopHeader from "./TopHeader.tsx"; // 明細一覧ヘッダーコンポーネント
import TopItemTable from "./TopItemTable.tsx"; // 明細一覧表コンポーネント
import TopUpdateModal from "./TopUpdateModal.tsx"; // 更新確定モーダル
import TopCsvImportModal from "./TopCsvImportModal.tsx"; // 明細CSV読込モーダル
import { getCategories, getItems, importItemsfromCsv, updateItems } from "./api/client.tsx";
import { toTopItemViewModelFromApiResponse, toTopHeaderViewModel} from "./type/P201GetItemsResponse.ts";
import type { TopHeaderViewModel, TopItemViewModel } from "./type/P201GetItemsResponse.ts";
import type { P301GetCategoriesResponseCategory } from "./type/P301GetCategoriesResponse.ts";
import { toApiString, fromApiString, type YearMonth } from "./type/YearMonth.ts";
import { type TopUpdateItem, type TopUpdatePatch, toTopUpdateItemsFromTopViewModel, toUpdateItemsRequestFromTop } from "./type/P202UpdateItemsRequest.ts";
import type { EditPatch, EditableField } from "./type/EditPatch.ts";
import { addMonths } from "./type/YearMonth.ts";
import { toCsvImportRequestFromTop } from "./type/P203ItemsCsvImportRequest.ts";
import { type P203ItemsCsvImportResponse } from "./type/P203ItemsCsvImportResponse.ts";
import { toast } from "react-toastify";
import { isWithinLimit } from "./utl/CharaWidth.ts";
import ServerError from "./ServerError.tsx";
import { type ScreenState } from "./type/ScreenState.ts";

function Top() {
  // 01:現在の表示年月
  const [targetYearMonth, setTargetYearMonth] = useState<YearMonth | null>(null);
  // 02:明細ヘッダーVM
  const [topHeaderViewModel, setTopHeaderViewModel] = useState<TopHeaderViewModel | null>(null);
  // 03:明細一覧VM
  const [topItemViewModels, setTopItemViewModels] = useState<TopItemViewModel[]>([]);
  // 04:画面の初期表示内容
  const [originalTopItemViewModel, setOriginalTopItemViewModel] = useState<TopItemViewModel[]>([]);
  // 05:カテゴリリスト
  const [categoryList, setCategoryList] = useState<P301GetCategoriesResponseCategory[]>([]);
  // 06:カテゴリフィルタリスト
  const [categoryFilterList, setCategoryFilterList] = useState<number[]>([]);
  // 07:画面の変更状態
  const [updateList, setUpdateList] = useState<TopUpdatePatch[]>([]);
  // 08:差分比較用のスナップショット
  const [originalUpdate, setOriginalUpdate] = useState<TopUpdatePatch[]>([]);
  // 09:明細タイトル必須のエラー検出
  const [isTitleEmptyError, setIsTitleEmptyError] = useState(false);
  // 10:明細タイトル文字数オーバーのエラー検出
  const [isTitleOverError, setIsTitleOverError] = useState(false);
  // 11:明細タイトル文字数オーバーのエラー検出
  const [isMemoOverError, setIsMemoOverError] = useState(false);
  // 12:明細更新確認モーダルの開閉管理フラグ
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  // 13:明細csv取込モーダルの開閉管理フラグ
  const [isCsvImportModalOpen, setIsCsvImportModalOpen] = useState(false);
  // 14:インポートされたファイル
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  // 15:CSVロード状態管理フラグ
  const [isCsvLoading, setIsCsvLoading] = useState(false);
  // 16:CSVインポート結果
  const [csvImportResult, setCsvImportResult] = useState<P203ItemsCsvImportResponse | null>(null);
  // 17:201+301APIエラーメッセージ
  const [errorMessage201Or301Api, setErrorMessage201Or301Api] = useState("");
  // 18:203APIエラーフラグ
  const [errorMessage203Api, setErrorMessage203Api] = useState("");
  // 19:画面の状態
  const [screenState, setScreenState] = useState<ScreenState>("checking");

  // 明細カテゴリ取得非同期実行
  useEffect(() => {
    // 明細カテゴリ取得
    const fetchGetCategories = async () => {
      try {
        const response = await getCategories();    
        if(response.categoryList != null){
          setCategoryList(response.categoryList);
        setCategoryFilterList([]);
        }
      } catch (e: unknown) {
        // 301_明細カテゴリ取得APIエラーレスポンス
        if (e instanceof Error) {
          setErrorMessage201Or301Api(e.message);
        } else {
          setErrorMessage201Or301Api("不明なエラーが発生しました");
        }
      }
    }
    // 実行
    fetchGetCategories();
  }, []);

  // 更新リストの比較元更新
  useEffect(() => {
    // 比較元のUpdateは比較元 VM が更新したら最新値に置き換える
    setOriginalUpdate(toTopUpdateItemsFromTopViewModel(originalTopItemViewModel));

  }, [originalTopItemViewModel]);

  // 明細Csv読込非同期実行
  useEffect(() => {
    // 明細Csv取込
    const fetchImportCsv = async (yearMonth: YearMonth, file: File) => {
      setIsCsvLoading(true);
      try {
        const response = await importItemsfromCsv(toCsvImportRequestFromTop(yearMonth, file));
        setCsvImportResult(response);
      } catch (e: unknown) {
        // 203_明細CSV読込APIエラーレスポンス
        if (e instanceof Error) {
          setErrorMessage203Api(e.message);
        } else {
          setErrorMessage203Api("不明なエラーが発生しました");
        }
      } finally {
        setSelectedFile(null);
        setIsCsvLoading(false);
      }
    }
    // 実行
    if(targetYearMonth !== null && selectedFile !== null) {
      fetchImportCsv(targetYearMonth, selectedFile);
    }
  }, [targetYearMonth, selectedFile]);

  // CSV読込結果を削除したらファイルも削除する
  useEffect(() => {
    if(csvImportResult === null){
      setSelectedFile(null);
    }
  }, [csvImportResult]);

  // 明細一覧のユーザ入力エラー検知
  useEffect(() => {
    // 明細タイトル必須エラー検出
    if(topItemViewModels.some((m) => m.title.trim() === "")) {
      setIsTitleEmptyError(true);
    } else {
      setIsTitleEmptyError(false);
    }
    // 明細タイトル文字数オーバーエラー検出
    if(topItemViewModels.some((m) => !isWithinLimit(m.title, 100))) {
      setIsTitleOverError(true);
    } else {
      setIsTitleOverError(false);
    }
    // 明細メモ文字数オーバーエラー検出
    if(topItemViewModels.some((m) => (!!m.memo && !isWithinLimit(m.memo, 200)))){
      setIsMemoOverError(true);
    } else {
      setIsMemoOverError(false);
    }
  }, [topItemViewModels]);

  // 前月の明細データを取得するハンドラ
  const handlePrev = () => {
    if(targetYearMonth != null) {
      fetchGetItems(addMonths(targetYearMonth, -1));
    } else {
      fetchGetItems();
    }
  };

  // 次月の明細データを取得するハンドラ
  const handleNext = () => {
    if(targetYearMonth != null) {
      fetchGetItems(addMonths(targetYearMonth, 1));
    } else {
      fetchGetItems();
    }
  };

  // カテゴリフィルタのオンオフを制御するハンドラ
  const handleFilter = ( categoryId: number ) => {
    // カテゴリフィルタリストに含まれていたら除外する
    if(categoryFilterList.includes(categoryId)){
      setCategoryFilterList(categoryFilterList => 
        categoryFilterList.filter(pre => pre !== categoryId));
    } else {
      // 含まれていなかったら追加する
      setCategoryFilterList(categoryFilterList => 
        [...categoryFilterList, categoryId]);
    }
  }

  // カテゴリフィルタを全解除するハンドラ
  const handleFilterClear = () => setCategoryFilterList([]);

  // 未確定変更をリセットするハンドラ
  const handleUnchange = () => {
    setTopItemViewModels([...originalTopItemViewModel]);
    setUpdateList([]);
  };

  /**
   * 変更差分をViewModelと差分状態に反映させる共通ハンドラ
   * @param itemId 
   * @param field 
   * @param rawValue 
   */
  const handleChange = (
    itemId: string,
    field: EditableField,
    rawValue: string
  ) => {
    const patch: EditPatch = { itemId };

    if (field === "title") {
      patch.title = rawValue;
    }

    if (field === "categoryId") {
      patch.categoryId = rawValue === "" ? undefined : Number(rawValue);
    }

    if (field === "memo") {
      patch.memo = rawValue;
    }

    // 分岐処理へ渡す
    applyEdit(patch);
  };

  /**
   * 明細一覧の変更をVMに反映させる
   * @param patch 
   */
  const updateItemViewModels = useCallback((patch : Partial<TopItemViewModel> &  { itemId: string }) => {
    setTopItemViewModels(prevModels => {
      const index = prevModels.findIndex(item => item.itemId === patch.itemId);

      if(index === -1){
        throw new Error("存在しない明細を更新しています。");
      }

      const copied = [...prevModels];
      copied[index] = { ...copied[index], ...patch }; // 元の値に差分を上書き

      return copied;
    });
  }, []);

  /**
   * 更新確定していない変更を保存する
   * @param patch 
   */
  const updateTopItems = useCallback((patch: TopUpdatePatch) => {
    setUpdateList(prevList => {
      // 比較元の初期明細
      const original = originalUpdate.find(o => o.itemId === patch.itemId);
      // 変更する更新配列のインデックス
      const index = prevList.findIndex(item => item.itemId === patch.itemId);

      // マージ後の更新配列
      const merged: TopUpdatePatch =
        index === -1
          // 該当のitemIdがなければ最初の配列をそのままセットする
          ? patch
          // 該当のitemIdがあれば
          : { ...prevList[index], ...patch};
      
      if (!original) {
        // 比較元が見つからない場合、そのまま保持する
        if(index === -1) {
          // 更新配列にもない場合、そのまま追加する
          return [...prevList, merged];
        }
        // 更新配列にある場合、そのインデックスを置き換える
        const nextList = [...prevList];
        nextList[index] = merged;
        return nextList;
      }
      // 最終更新格納用
      const cleaned: TopUpdatePatch = { itemId: merged.itemId };

      // title
      if (merged.title !== undefined && original.title !== merged.title) {
        cleaned.title = merged.title;
      }

      // categoryId
      if (merged.categoryId !== undefined && original.categoryId !== merged.categoryId) {
        cleaned.categoryId = merged.categoryId;
      }

      // memo
      if (merged.memo !== undefined && original.memo !== merged.memo) {
        cleaned.memo = merged.memo;
      }

      // 最終更新格納用
      // const cleaned: TopUpdatePatch = { itemId: merged.itemId };
      // const keys: (keyof TopUpdatePatch)[] = ["title", "categoryId", "memo"];
      // keys.forEach((key) => {
      //   if (original[key] !== merged[key]) {
      //     // 一時的な例外として、意図を明確にコメント
      //     // cleaned as anyがエラーにならないように
      //     // eslint-disable-next-line @typescript-eslint/no-explicit-any
      //     (cleaned as any)[key] = merged[key] as TopUpdatePatch[typeof key];
      //   }
      // });

      // itemId以外のキーが残っていないなら差分がないのでupdateListから削除
      const hasAnyDiff = Object.keys(cleaned).some(key => key !== "itemId");
      if (!hasAnyDiff) {
        return prevList.filter(p => p.itemId !== patch.itemId);
      }

      // 差分がある場合はupdateListを更新する
      if (index === -1) {
        // 当てはまるitemIdが無い場合はそのまま追加
        return [...prevList, cleaned];
      } else {
        // 当てはまるitemIdがある場合はそのインデックスを置き換える
        const nextList = [...prevList];
        nextList[index] = cleaned as TopUpdatePatch;
        return nextList;
      }
    });
  }, [originalUpdate]);

  /**
   * 変更差分をViewModelと差分状態に反映させる共通処理
   * @param patch 
   */
  const applyEdit = useCallback((patch: EditPatch) => {
    // ItemViewModel
    const viewPatch: Partial<TopItemViewModel> & { itemId: string } = {
      itemId: patch.itemId,
      ...(patch.title !== undefined ? { title: patch.title } : {}),
      ...(patch.categoryId !== undefined ? { majorCategoryId: patch.categoryId } : {}),
      ...(patch.memo !== undefined ? { memo: patch.memo } : {}),
    };

    // API用
    const apiPatch: TopUpdateItem = {
      itemId: patch.itemId,
      ...(patch.title !== undefined ? { title: patch.title } : {}),
      ...(patch.categoryId !== undefined ? { categoryId: patch.categoryId } : {}),
      ...(patch.memo !== undefined ? { memo: patch.memo } : {}),
    };

    // ItemViewModelの更新
    updateItemViewModels(viewPatch);
    // 差分状態更新
    updateTopItems(apiPatch);
  }, [updateItemViewModels, updateTopItems]);

  /**
   * 存在しないカテゴリが割り振られていないか確認。
   * 割り振られていたら未設定として強制更新モードに移行。
   */
  const checkItemCategory = useCallback((categories: Set<number>, items: TopItemViewModel[]) => {
    setScreenState("checking");
    // カテゴリから外れている明細を抽出
    const outOfCategoryItems = items.filter(o => !categories.has(o.majorCategoryId));
    // もし上記明細が存在したら、強制更新モードに画面を変更
    if(outOfCategoryItems.length > 0) {
      outOfCategoryItems.forEach(item => 
        applyEdit({
          itemId: item.itemId, 
          categoryId: 0,
        })
      );
      setScreenState("ForceUpdate");
    } else {
      // 該当する明細がなければ、強制更新モードを解除
      setScreenState("OK");
    }
  }, [applyEdit]);

  // カテゴリリストに存在するカテゴリが設定されているか検知する
  useEffect(() => {
    if(categoryList !== null && originalTopItemViewModel !== null) {
      checkItemCategory(new Set(categoryList.map(c => c.categoryId)), originalTopItemViewModel);
    }
  }, [checkItemCategory, categoryList, originalTopItemViewModel]);

  // 明細一覧取得
  const fetchGetItems = async (yearMonth? : YearMonth) => {
    try{
      setScreenState("Loading");
      const response = yearMonth ? await getItems(toApiString(yearMonth)) : await getItems();
      const targetYearMonth = fromApiString(response.yearMonth);
      const topItemViewModels = toTopItemViewModelFromApiResponse(response);
      const topHeaderViewModel = toTopHeaderViewModel(response);
      setTargetYearMonth(targetYearMonth);
      setTopHeaderViewModel(topHeaderViewModel)
      setTopItemViewModels(topItemViewModels);
      setUpdateList([]);
      setOriginalTopItemViewModel([...topItemViewModels]);
    } catch (e: unknown) {
      // 201_明細一覧取得エラーレスポンス
      if (e instanceof Error) {
        setErrorMessage201Or301Api(e.message);
      } else {
        setErrorMessage201Or301Api("不明なエラーが発生しました");
      }
      setScreenState("ERROR");
    }
  }

  // 明細一覧取得非同期実行
  useEffect(() => {
    fetchGetItems();
  }, []);

  // 明細更新確定モーダルを開くハンドラ
  const handleOpenUpdateModal = () => {setIsUpdateModalOpen(true)};
  // 明細更新確定モーダルを閉じるハンドラ
  const handleCloseUpdateModal = () => {setIsUpdateModalOpen(false)};
  // 明細更新処理を行うハンドラ
  const handleUpdate = async () => {
    setIsUpdateModalOpen(false)
    // 明細更新API実行
    if(targetYearMonth !== null){
      // 更新中トースト表示
      toast.info("明細更新中です・・・");
      try {
        const response = await updateItems(toUpdateItemsRequestFromTop(targetYearMonth, updateList));
        // レスポンスから各明細のアップデート成否を受け取る
        const resultList = response.updateResultList;
        // ① API結果で成功・失敗を判定
        const successIds = new Set(resultList.filter(r => r.status).map(r => r.itemId));
        const failureIds = new Set(resultList.filter(r => !r.status).map(r => r.itemId));

        // ② 更新リストは「失敗したもの」と「成功していないもの」に絞る
        setUpdateList(prev =>
          prev.filter(item => failureIds.has(item.itemId) || !successIds.has(item.itemId))
        );

        // ③ 比較元の VM は「成功したものだけ」最新値に置き換える
        setOriginalTopItemViewModel(originalTopItemViewModel.map(orig => {
          if (!successIds.has(orig.itemId)) {
            return orig; // 失敗した・未対象のものはそのまま
          }
          // 今画面に表示している状態から、該当IDの最新値を取ってくるイメージ
          const latest = topItemViewModels.find(i => i.itemId === orig.itemId);
          return latest ?? orig;
        }));
        // 更新失敗トースト表示
        if(failureIds.size > 0){
          toast.error(
            <div>
              一部更新に失敗しました<br />
              もう一度更新確定してください
            </div>);
        } else {
          // 更新完了トースト表示
          toast.success("更新完了しました");
        }
      } catch (e: unknown) {
        // 202_明細更新APIエラーレスポンス
        if (e instanceof Error) {
          toast.error(<div>
              更新に失敗しました<br />
              もう一度更新確定してください
            </div>);
        } else {
          toast.error("不明なエラーが発生しました");
        }
      }
    }
  }

  // 明細Csv取込モーダルを開くハンドラ
  const handleOpenCsvImportModal = () => {setIsCsvImportModalOpen(true)};
  // 明細Csv取込モーダルを閉じるハンドラ
  const handleCloseCsvImportModal = () => {
    setIsCsvImportModalOpen(false);
    // モーダルを閉じた場合は読込結果を削除する
    setCsvImportResult(null);
    // エラーメッセージも削除する
    setErrorMessage203Api("");
  };
  // 明細Csvファイル取込ハンドラ
  const handleFileSelected = (file: File) => {
    setSelectedFile(file);
  }
    
  return (
    <>
      {errorMessage201Or301Api !== "" ? (
        // APIエラー画面
        <ServerError errorMessage={errorMessage201Or301Api}/>
      ) : (
          <>
            {/* 明細ヘッダー */}
            <TopHeader targetYearMonth={targetYearMonth}
              topHeaderViewModel={topHeaderViewModel}
              categoryList={categoryList}
              categoryFilterList={categoryFilterList}
              isUpdateList={updateList.length != 0}
              isTitleEmptyError={isTitleEmptyError}
              isTitleOverError={isTitleOverError}
              isMemoOverError={isMemoOverError}
              screenState={screenState}
              onClickPrevMonth={handlePrev}
              onClickNextMonth={handleNext}
              onClickFilter={handleFilter}
              onClickUnFilter={handleFilterClear}
              onClickOpenUpdateModal={handleOpenUpdateModal}
              onClickUnchange={handleUnchange}
              onClickOpenCsvImportModal={handleOpenCsvImportModal}/>
            {/* 明細一覧表 */}
            <TopItemTable 
              topItemViewModels={topItemViewModels} 
              categoryList={categoryList} 
              categoryFilterList={categoryFilterList}
              updateList={updateList}
              screenState={screenState}
              onChangeItem={handleChange}/>
            {/* 明細更新確認モーダル */}
            <TopUpdateModal
              open={isUpdateModalOpen}
              updateNum={updateList.length}
              onClose={handleCloseUpdateModal}
              onUpdate={handleUpdate}/>
            {/* 明細Csv読込モーダル */}
            <TopCsvImportModal 
              open={isCsvImportModalOpen}
              csvLoading={isCsvLoading}
              csvImportResult={csvImportResult}
              selectedFile={selectedFile}
              errorMessage203Api={errorMessage203Api}
              onClose={handleCloseCsvImportModal}
              onFileSelected={handleFileSelected}/>
          </>
      )}
    </>
  );
}

export default Top;
