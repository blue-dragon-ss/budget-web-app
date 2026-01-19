import type { EditableField } from "./type/EditPatch.ts";
import type { TopItemViewModel } from "./type/P201GetItemsResponse.ts";
import type { P301GetCategoriesResponseCategory } from "./type/P301GetCategoriesResponse.ts";
import { type TopUpdatePatch } from "./type/P202UpdateItemsRequest.ts";
import { isForceUpdateScreen, isLoadingScreen, type ScreenState } from "./type/ScreenState.ts";
type Props = {
  // 親から受け取る明細データVM
  topItemViewModels: TopItemViewModel[];
  // 親から受け取るカテゴリデータ
  categoryList?: P301GetCategoriesResponseCategory[];
  // カテゴリフィルタ
  categoryFilterList: number[];
  // 画面の更新状態
  updateList: TopUpdatePatch[];
  // 強制更新モード
  screenState: ScreenState;
  // 明細一覧からVMの更新
  onChangeItem: (itemId: string,
    field: EditableField,
    rawValue: string) => void;
};

function TopItemTable({topItemViewModels,
    updateList,
    categoryList,
    categoryFilterList,
    screenState,
    onChangeItem}: Props) {

  // 表示フラグ
  const isDisplay = !isLoadingScreen(screenState) && topItemViewModels.length != 0;
  // 更新リストの明細ID
  const updateIdSet = new Set(updateList.map(u => u.itemId));
  // 更新リストに該当の明細があるか
  const hasUpdate = (itemId: string) => updateIdSet.has(itemId);
  // カテゴリリストがあるか
  const hasCategory = !!categoryList && categoryList.length > 0;

  return (
    <> 
      {isDisplay && 
       (isForceUpdateScreen(screenState) ? 
        (<p className="topItemTable__forceMessage">
          データのアップデートが必要な状態です<br />
          更新確定ボタンを押して更新させてください
        </p>
        ) : (
        <div className="topItemTable">
          {/* 見出しはスクロール領域の外に出して固定する */}
          <div className="topItemTable__head">
            <table className="topItemTable__table topItemTable__tableHead">
              {/* 列幅ズレ防止のため、ヘッダー/本文で同じcolgroupを使う */}
              <colgroup>
                <col className="topItemTable__colStatus" />
                <col className="topItemTable__colDate" />
                <col className="topItemTable__colTitle" />
                <col className="topItemTable__colMajor" />
                <col className="topItemTable__colMinor" />
                <col className="topItemTable__colMemo" />
                <col className="topItemTable__colAmount" />
              </colgroup>

              <thead className="topItemTable__thead">
                <tr className="topItemTable__tr" key="thead">
                  <td className="topItemTable__th topItemTable__colStatus">未確定</td>
                  <td className="topItemTable__th topItemTable__colDate">取引日</td>
                  <td className="topItemTable__th topItemTable__colTitle">明細タイトル</td>
                  <td className="topItemTable__th topItemTable__colMajor">大カテゴリ</td>
                  <td className="topItemTable__th topItemTable__colMinor">小カテゴリ</td>
                  <td className="topItemTable__th topItemTable__colMemo">メモ</td>
                  <td className="topItemTable__th topItemTable__colAmount">当月支払金額</td>
                </tr>
              </thead>
            </table>
          </div>

          {/* 本文だけスクロールさせる */}
          <div className="topItemTable__scroll">
            <table className="topItemTable__table topItemTable__tableBody">
              {/* 列幅ズレ防止のため、ヘッダー/本文で同じcolgroupを使う */}
              <colgroup>
                <col className="topItemTable__colStatus" />
                <col className="topItemTable__colDate" />
                <col className="topItemTable__colTitle" />
                <col className="topItemTable__colMajor" />
                <col className="topItemTable__colMinor" />
                <col className="topItemTable__colMemo" />
                <col className="topItemTable__colAmount" />
              </colgroup>

              <tbody className="topItemTable__tbody">
                {topItemViewModels
                .filter((topItemViewModel) => !categoryList || categoryFilterList.length == 0 ||
                  categoryFilterList.includes(topItemViewModel.majorCategoryId))
                .map((topItemViewModel) => (
                  // 更新がある場合はその行に色を付ける
                  <tr  className={`topItemTable__tr ${hasUpdate(topItemViewModel.itemId) ? "topItemTable__tr--dirty" : ""}`}
                    key={topItemViewModel.itemId}>
                    {/* 未更新フラグ */}
                    <td className="topItemTable__td topItemTable__colStatus">
                      {hasUpdate(topItemViewModel.itemId)
                        ? (
                        <span className="topItemTable__statusMark">!</span>
                      ) : (
                        <span className="topItemTable__statusMuted">-</span>
                      )}
                    </td>
                    {/* 取引日 */}
                    <td className="topItemTable__td topItemTable__colDate">
                      {topItemViewModel.date.toLocaleDateString()}
                    </td>
                    {/* 明細タイトル */}
                    <td className="topItemTable__td topItemTable__colTitle">
                      <input
                        className="topItemTable__input" 
                        type="text" name="title" 
                        value={topItemViewModel.title}
                        onChange={(e) => {onChangeItem(
                          topItemViewModel.itemId,
                          "title",
                          e.target.value,
                        )}}
                      />
                    </td>
                    {/* 大カテゴリ */}
                    <td className="topItemTable__td topItemTable__colMajor">
                      {hasCategory ? (
                        <select className="topItemTable__select" name="majorCateogry"
                          value={topItemViewModel.majorCategoryId}
                          onChange={(e) => {onChangeItem(
                            topItemViewModel.itemId,
                            "categoryId",
                            e.target.value,
                          )}}
                        >
                        {categoryList.map((category) => (
                            <option key={category.categoryId} value={category.categoryId}>{category.categoryName}</option>
                          ))}
                        </select>
                      ) : (
                        <>-</>
                      )}
                    </td>
                    {/* 小カテゴリ */}
                    <td className="topItemTable__td topItemTable__colMinor">
                      -
                    </td>
                    {/* メモ */}
                    <td className="topItemTable__td topItemTable__colMemo">
                      <input className="topItemTable__input" 
                      type="text" name="memo" 
                        value={topItemViewModel.memo}
                        onChange={(e) => {onChangeItem(
                            topItemViewModel.itemId,
                            "memo",
                            e.target.value,
                          )}}
                        ></input>
                    </td>
                    {/* 当月支払金額 */}
                    <td className="topItemTable__td topItemTable__colAmount">
                      {topItemViewModel.amount.toLocaleString("ja-JP")} 円
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ))}
    </>
  );
}

export default TopItemTable;
