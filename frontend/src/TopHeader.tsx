import { ERROR_MESSAGES } from "./type/error/ErrorMessages.ts";
import type { TopHeaderViewModel } from "./type/P201GetItemsResponse.ts";
import type { P301GetCategoriesResponseCategory } from "./type/P301GetCategoriesResponse.ts";
import { type ScreenState, isErrorScreen, isForceUpdateScreen, isLoadingScreen } from "./type/ScreenState.ts";
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

function TopHeader({
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
  onClickOpenCsvImportModal,
}: Props) {
  // 表示フラグ
  const isDisplay = !isErrorScreen(screenState) && targetYearMonth != null && topHeaderViewModel != null;
  // カテゴリリストがあるか
  const hasCategory = !!categoryList && categoryList.length > 0;
  // 入力エラーがあるか
  const isInputError = isTitleEmptyError || isTitleOverError || isMemoOverError;
  // 変更状態が存在する場合に表示する
  const hasAlerts =
    (isUpdateList && !isForceUpdateScreen(screenState)) ||
    (isUpdateList && isTitleEmptyError) ||
    (isUpdateList && isTitleOverError) ||
    (isUpdateList && isMemoOverError);

  return (
    <>
      {isDisplay && (
        <header className="topHeader">
          <div className="topHeader__layout">
            <h1 className="topHeader__title">
              {/* タイトル */}
              明細一覧
            </h1>

            <h2 className="topHeader__monthRow">
              {/* 前月遷移ボタン */}
              <button
                className="btn topHeader__monthBtn"
                key="prevMonth"
                name="prevMonth"
                value="前月"
                disabled={isLoadingScreen(screenState)}
                onClick={onClickPrevMonth}
              >
                前月
              </button>
              {/* 年月 */}
              <span className="topHeader__monthLabel">
                {targetYearMonth.year} 年 {targetYearMonth.month} 月
              </span>
              {/* 次月遷移ボタン */}
              <button
                className="btn topHeader__monthBtn"
                key="nextMonth"
                name="nextMonth"
                value="次月"
                disabled={isLoadingScreen(screenState)}
                onClick={onClickNextMonth}
              >
                次月
              </button>
            </h2>

            <div className="topHeader__summaryArea">
              {/* 件数が0でないなら件数と合計支払金額表示 */}
              {topHeaderViewModel.totalNum != 0 && (
                <h3 className="topHeader__summary">
                  <span className="topHeader__summaryCount">
                    全{" "}
                    <span className="topHeader__summaryNum">{topHeaderViewModel.totalNum.toLocaleString("ja-JP")}</span>{" "}
                    件
                  </span>

                  <span className="topHeader__summaryTotal">
                    支払合計{" "}
                    <span className="topHeader__summaryAmount">
                      {topHeaderViewModel.totalAmount.toLocaleString("ja-JP")}
                    </span>{" "}
                    円
                  </span>
                </h3>
              )}
              {/* 件数が0なら明細0件表示 */}
              {topHeaderViewModel.totalNum === 0 && (
                <h3 className="topHeader__summary">当月に明細データがありません</h3>
              )}
            </div>

            <div className="topHeader__actionsStack">
              {/* CSV読込モーダルは更新リストが無いかロード中以外なら活性 */}
              <button
                className="btn"
                type="submit"
                name="csvImport"
                onClick={onClickOpenCsvImportModal}
                disabled={isUpdateList || isLoadingScreen(screenState)}
              >
                CSV読込
              </button>
              {/* 更新モーダルは更新リストがあり、エラーがないロード中以外なら活性 */}
              <button
                className="btn btn-primary"
                key="update"
                type="button"
                name="update"
                disabled={!isUpdateList || isInputError || isLoadingScreen(screenState)}
                onClick={onClickOpenUpdateModal}
              >
                更新確定
              </button>
              {/* 変更リセットボタンは更新リストがあるか強制更新モードでないロード中以外なら活性 */}
              <button
                className="btn btn-ghost"
                key="unchange"
                type="button"
                name="unchange"
                disabled={!isUpdateList || isForceUpdateScreen(screenState) || isLoadingScreen(screenState)}
                onClick={onClickUnchange}
              >
                変更リセット
              </button>
            </div>
          </div>

          {/* カテゴリリストがあればフィルタボタンを表示 */}
          {hasCategory && (
            <div className="topHeader__filters">
              <div className="topHeader__filtersLabel">フィルタ設定</div>
              <div className="topHeader__chips">
                {categoryList.map((category) => (
                  <button
                    className={`chip ${categoryFilterList.includes(category.categoryId) ? "is-active" : ""}`}
                    key={category.categoryName}
                    type="button"
                    name={category.categoryName}
                    value={category.categoryId}
                    onClick={() => onClickFilter(category.categoryId)}
                    disabled={isLoadingScreen(screenState)}
                  >
                    {category.categoryName}
                  </button>
                ))}
                <button
                  className="btn btn-ghost topHeader__filterClear"
                  key="filterClear"
                  type="button"
                  name="unFilter"
                  disabled={categoryFilterList.length === 0 || isLoadingScreen(screenState)}
                  onClick={onClickUnFilter}
                >
                  フィルタ全解除
                </button>
              </div>
            </div>
          )}

          {hasAlerts && (
            <div className="topHeader__alerts">
              {/* 変更状態が存在する場合に表示する */}
              {isUpdateList && !isForceUpdateScreen(screenState) && (
                <div className="topHeader__alertItem">{ERROR_MESSAGES["TOP.UPDATE_PENDING"]}</div>
              )}
              {/* 変更状態が存在し、明細タイトルが存在しない場合に表示する */}
              {isUpdateList && isTitleEmptyError && (
                <div className="topHeader__alertItem">{ERROR_MESSAGES["ITEM_TITLE.REQUIRED"]}</div>
              )}
              {/* 変更状態が存在し、明細タイトルが文字数オーバーしている場合に表示する */}
              {isUpdateList && isTitleOverError && (
                <div className="topHeader__alertItem">{ERROR_MESSAGES["ITEM_TITLE.MAX_LENGTH"]}</div>
              )}
              {/* 変更状態が存在し、明細メモが文字数オーバーしている場合に表示する */}
              {isUpdateList && isMemoOverError && (
                <div className="topHeader__alertItem">{ERROR_MESSAGES["ITEM_MEMO.MAX_LENGTH"]}</div>
              )}
            </div>
          )}
        </header>
      )}
    </>
  );
}

export default TopHeader;
