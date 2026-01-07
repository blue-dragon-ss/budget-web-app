import "./TopItemTable.css";
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
        (<p>
          データのアップデートが必要な状態です<br />
          更新確定ボタンを押して更新させてください
        </p>
        ) : (
        <table>
          <thead>
            <tr key="thead">
              <td>未確定</td>
              <td>取引日</td>
              <td>明細タイトル</td>
              <td>大カテゴリ</td>
              <td>小カテゴリ</td>
              <td>メモ</td>
              <td>当月支払金額</td>
            </tr>
          </thead>
          <tbody>
            {topItemViewModels
            .filter((topItemViewModel) => !categoryList || categoryFilterList.length == 0 ||
              categoryFilterList.includes(topItemViewModel.majorCategoryId))
            .map((topItemViewModel) => (
              <tr key={topItemViewModel.itemId}>
                {/* 未更新フラグ */}
                <td>
                  {hasUpdate(topItemViewModel.itemId)
                     ? (
                    <>!</>
                  ) : (
                    <>-</>
                  )}
                </td>
                {/* 取引日 */}
                <td>
                  {topItemViewModel.date.toLocaleDateString()}
                </td>
                {/* 明細タイトル */}
                <td>
                  <input type="text" name="title" 
                  value={topItemViewModel.title}
                  onChange={(e) => {onChangeItem(
                    topItemViewModel.itemId,
                    "title",
                    e.target.value,
                  )}}
                  ></input>
                </td>
                {/* 大カテゴリ */}
                <td>
                  {hasCategory ? (
                    <select name="majorCateogry"
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
                <td>
                  -
                </td>
                {/* メモ */}
                <td>
                  <input type="text" name="memo" 
                    value={topItemViewModel.memo}
                    onChange={(e) => {onChangeItem(
                        topItemViewModel.itemId,
                        "memo",
                        e.target.value,
                      )}}
                    ></input>
                </td>
                {/* 当月支払金額 */}
                <td>
                  {topItemViewModel.amount.toLocaleString("ja-JP")} 円
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ))}
    </>
  );
}

export default TopItemTable;
