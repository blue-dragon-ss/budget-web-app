import "./TopHeader.css";
import { ERROR_MESSAGES } from "./type/error/ErrorMessages.ts";
import type { TopHeaderViewModel } from "./type/P201GetItemsResponse.ts";
import type { P301GetCategoriesResponseCategory } from "./type/P301GetCategoriesResponse.ts";
import  { type ScreenState, isErrorScreen, isForceUpdateScreen, isLoadingScreen } from "./type/ScreenState.ts";
import type { YearMonth } from "./type/YearMonth.ts";

type Props = {
  targetYearMonth: YearMonth | null;
  topHeaderViewModel: TopHeaderViewModel | null;
  categoryList: P301GetCategoriesResponseCategory[];
  categoryFilterList: number[];
  isUpdateList: boolean;
  isTitleEmptyError: boolean;
  isTitleOverError: boolean;
  isMemoOverError: boolean;
  screenState: ScreenState;
  onClickPrevMonth: () => void;
  onClickNextMonth: () => void;
  onClickFilter: (categoryId: number) => void;
  onClickUnFilter: () => void;
  onClickOpenUpdateModal: () => void;
  onClickUnchange: () => void;
  onClickOpenCsvImportModal: () => void;
};

function TopItemTable({
  targetYearMonth,
  topHeaderViewModel,
  categoryList,
  categoryFilterList,
  isUpdateList,
  isTitleEmptyError,
  isTitleOverError,
  isMemoOverError,
  screenState,
  onClickPrevMonth,
  onClickNextMonth,
  onClickFilter,
  onClickUnFilter,
  onClickOpenUpdateModal,
  onClickUnchange,
  onClickOpenCsvImportModal}: Props) {

  // 表示フラグ
  const isDisplay = !isErrorScreen(screenState) 
    && targetYearMonth != null 
    && topHeaderViewModel != null;
  // カテゴリリストがあるか
  const hasCategory = !!categoryList && categoryList.length > 0;
  // 入力エラーがあるか
  const isInputError = isTitleEmptyError || isTitleOverError || isMemoOverError;

  return (
    <>
      {isDisplay && (
        <header>
          <h1>
            {/* タイトル */}
            明細データ一覧
          </h1>
          <h2>
            {/* 前月遷移ボタン */}
            <button key="prevMonth" name="prevMonth" value="前月" disabled={isLoadingScreen(screenState)}
              onClick={onClickPrevMonth}>前月</button>
            {/* 年月 */}
            {targetYearMonth.year} 年 {targetYearMonth.month} 月
            {/* 次月遷移ボタン */}
            <button key="nextMonth" name="nextMonth" value="次月" disabled={isLoadingScreen(screenState)}
              onClick={onClickNextMonth}>次月</button>
          </h2>
         
          {/* 件数が0でないなら件数と合計支払金額表示 */}
          {topHeaderViewModel.totalNum != 0 && (
            <h3>全 {topHeaderViewModel.totalNum} 件
              支払合計 {topHeaderViewModel.totalAmount.toLocaleString("ja-JP")} 円</h3>
          )}
          {/* 件数が0なら明細0件表示 */}
          {topHeaderViewModel.totalNum === 0 && (
            <h3>当月に明細データがありません</h3>
          )}
          {/* 変更状態が存在する場合に表示する */}
          {isUpdateList && !isForceUpdateScreen(screenState)
            && <h4>{ERROR_MESSAGES["TOP.UPDATE_PENDING"]}</h4>}
          {/* 変更状態が存在し、明細タイトルが存在しない場合に表示する */}
          {isUpdateList && isTitleEmptyError
            && <h4>{ERROR_MESSAGES["ITEM_TITLE.REQUIRED"]}</h4>}
          {/* 変更状態が存在し、明細タイトルが文字数オーバーしている場合に表示する */}
          {isUpdateList && isTitleOverError
            && <h4>{ERROR_MESSAGES["ITEM_TITLE.MAX_LENGTH"]}</h4>}
          {/* 変更状態が存在し、明細メモが文字数オーバーしている場合に表示する */}
          {isUpdateList && isMemoOverError
            && <h4>{ERROR_MESSAGES["ITEM_MEMO.MAX_LENGTH"]}</h4>}
          <p>
            {/* CSV読込モーダルは更新リストが無いかロード中以外なら活性 */}
            <button type="submit" name="csvImport" onClick={onClickOpenCsvImportModal}
             disabled={isUpdateList || isLoadingScreen(screenState)}>CSV読込</button>
            {/* 更新モーダルは更新リストがあり、エラーがないロード中以外なら活性 */}
            <button key="update" type="button" name="update"
             disabled={!isUpdateList || isInputError || isLoadingScreen(screenState)}
              onClick={onClickOpenUpdateModal}>
                更新確定
            </button>
            {/* 変更リセットボタンは更新リストがあるか強制更新モードでないロード中以外なら活性 */}
            <button key="unchange" type="button" name="unchange"
             disabled={!isUpdateList || isForceUpdateScreen(screenState) || isLoadingScreen(screenState)}
              onClick={onClickUnchange}>変更リセット</button>
          </p>
          {/* カテゴリリストがあればフィルタボタンを表示 */}
          {hasCategory && (
            <>
              フィルタ設定
              {categoryList.map((category) => (
                <button key={category.categoryName} type="button"
                  name={category.categoryName} value={category.categoryId}
                  onClick={() => onClickFilter(category.categoryId)}
                  disabled={isLoadingScreen(screenState)}>
                  {category.categoryName}</button>
              ))}
              <button key="filterClear" type="button" name="unFilter"
               disabled={categoryFilterList.length === 0 || isLoadingScreen(screenState)}
                onClick={onClickUnFilter}>フィルタ全解除</button>
            </> 
          )}
        </header>
      )}
    </>
  );
}

export default TopItemTable;
