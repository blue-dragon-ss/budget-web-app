package com.example.minimal.item;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minimal.category.CategoryEntity;
import com.example.minimal.category.CategoryRepository;
import com.example.minimal.common.TraceIdHolder;
import com.example.minimal.common.constants.CategoryId;
import com.example.minimal.common.constants.FixedMemberId;
import com.example.minimal.common.constants.Formats;
import com.example.minimal.common.constants.SQLState;
import com.example.minimal.common.exception.DuplicateValueException;
import com.example.minimal.common.exception.UnexpectedPersistenceException;
import com.example.minimal.common.exception.error.BusinessException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.common.exception.error.ValidationException;
import com.example.minimal.common.util.CsvReader;
import com.example.minimal.common.util.CsvReader.CsvData;
import com.example.minimal.common.util.CsvReader.CsvParseError;
import com.example.minimal.common.util.CsvReader.CsvRow;
import com.example.minimal.common.util.SQLUtils;
import com.example.minimal.common.util.StringUtils;
import com.example.minimal.item.dto.ItemOverViewWithCategoryDto;
import com.example.minimal.item.dto.P201Response;
import com.example.minimal.item.dto.P201ResponseItem;
import com.example.minimal.item.dto.P202Request;
import com.example.minimal.item.dto.P202RequestUpdateItem;
import com.example.minimal.item.dto.P202Response;
import com.example.minimal.item.dto.P202ResponseUpdateResult;
import com.example.minimal.item.dto.P203Request;
import com.example.minimal.item.dto.P203Response;
import com.example.minimal.item.dto.P203ResponseError;
import com.example.minimal.member.MemberRepository;
import com.github.f4b6a3.ulid.UlidCreator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final CategoryRepository categoryRepository;
	private final MemberRepository memberRepository;
	private final String UQ_ITEMS_CODE_ACTIVE = "uq_items_public_id_active";

	@PersistenceContext
	private EntityManager entityManager;

	public ItemService(
			ItemRepository itemRepository, CategoryRepository categoryRepository, MemberRepository memberRepository) {
		this.itemRepository = itemRepository;
		this.categoryRepository = categoryRepository;
		this.memberRepository = memberRepository;
	}

	private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern(Formats.YEAR_MONTH);
	private static final DateTimeFormatter YEAR_MONTH_DB_FORMATTER = DateTimeFormatter.ofPattern(Formats.YEAR_MONTH_DB);
	private static final DateTimeFormatter USAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern(Formats.USAGE_DATE);

	// 更新失敗メッセージ
	private static final String UPDATE_NOT_FOUND = "NOT_FOUND";

	@Transactional
	public P201Response getItems(String yearMonth) {
		// 3.URLパラメータの「年月」を「-」で分割（#1）
		// ①バリデーションチェック（範囲）
		YearMonth targetYearMonth = YearMonth.parse(yearMonth, YEAR_MONTH_FORMATTER);

		// 会員IDの存在チェック（FK制約違反回避のため事前にチェック）
		memberRepository.findByIdAndDeletedAtIsNull(FixedMemberId.FIXED_MEMBER_ID)
				.orElseThrow(() -> new BusinessException("memberId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
						ErrorCode.ITM_BUS_BAD_FOREIGN_KEY));

		// DB用に年月を「yyyyMM」に変更
		String targetYearMonthDB = targetYearMonth.format(YEAR_MONTH_DB_FORMATTER);

		List<ItemOverViewWithCategoryDto> items = Collections.emptyList();
		BigDecimal totalAmount = BigDecimal.ZERO;

		try {
			// 4.以下の処理でDBからデータを取得
			// ①「DB02_明細」から「支払年月」=「年月」と「会員ID」でデータを検索（#2）
			// ②「DB02_明細.カテゴリID = DB03_カテゴリマスタ.カテゴリ主キー」の条件で
			// ①に取得したデータに「DB02_明細.カテゴリローカルID」を関連付け（#4）
			// ※会員IDは固定のものを使用する
			items = itemRepository.findActiveItemsOverViewWithCategory(FixedMemberId.FIXED_MEMBER_ID,
					targetYearMonthDB);

			// この際、検索結果件数と「当月支払金額」の合計も算出（#3）
			if (items.size() > 0) {
				totalAmount = itemRepository.sumUsageAmount(FixedMemberId.FIXED_MEMBER_ID, targetYearMonthDB);
			}
		} catch (DataIntegrityViolationException e) {
			// 想定外の永続化エラーは自前の500用例外に正規化して再投げ
			throw new UnexpectedPersistenceException(null, ErrorMessage.COM_SERVER_ERROR_MESSAGE,
					ErrorCode.COM_SERVER_ERROR, e);
		}

		// ③「アウトプット元」に書かれているデータを取得
		// 5.レスポンスを返却
		// 成功時：登録データを返却（traceId含む）
		// 上記レスポンス仕様の形に整形
		// 失敗時：例外コード変換し共通フォーマットで応答
		return toP201Response(yearMonth, items, totalAmount.longValue());
	}

	@Transactional
	public P202Response update(P202Request request) {
		// 会員IDの存在チェック（FK制約違反回避のため事前にチェック）
		memberRepository.findByIdAndDeletedAtIsNull(FixedMemberId.FIXED_MEMBER_ID)
				.orElseThrow(() -> new BusinessException("memberId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
						ErrorCode.ITM_BUS_BAD_FOREIGN_KEY));

		// 2. バリデーションチェック（必須・長さ・形式・範囲）
		// ①リクエストボディの「年月」を「-」で分割しバリデーションチェック（範囲）
		YearMonth targetYearMonth = YearMonth.parse(request.getYearMonth(), YEAR_MONTH_FORMATTER);

		// 3.DTOの「更新件数」と「更新リスト」の長さが一致しているかチェック：BUS-20201
		int totalNum = request.getTotalNum();
		List<P202RequestUpdateItem> updateItems = request.getUpdateItemList();
		if (totalNum != updateItems.size()) {
			throw new BusinessException(null, ErrorMessage.ITM_NOT_MATCH_SIZE_UPDATE_LIST,
					ErrorCode.ITM_BUS_UPDATE_LIST_SIZE_NOT_MATCH);
		}

		// 更新用
		List<ItemEntity> uiel = new ArrayList<ItemEntity>();

		// 4.各明細について、以下のチェックを行う
		for (P202RequestUpdateItem updateItem : updateItems) {
			// 更新要素存在確認フラグ
			boolean isUpdateColumnExist = false;
			ItemEntity i = new ItemEntity();
			// 明細ID
			i.setPublicId(updateItem.getItemId());
			// 利用日
			if (updateItem.getDate() != null) {
				LocalDate d = StringUtils.parseDateOrNull(updateItem.getDate());
				// 年月日変換ができていなければ範囲外エラーを投げる
				if (d == null) {
					throw new ValidationException("date", ErrorMessage.VAL_YEAR_MONTH_DATE_OUT_RANGE,
							ErrorCode.ITM_VAL_RANGE_DATE);
				}
				i.setUsageDate(d);
				isUpdateColumnExist = true;
			}
			// 明細タイトル
			if (updateItem.getTitle() != null) {
				i.setTitle(updateItem.getTitle());
				isUpdateColumnExist = true;
			}
			// 利用者
			if (updateItem.getPayer() != null) {
				i.setPayer(updateItem.getPayer());
				isUpdateColumnExist = true;
			}
			// 支払方法
			if (updateItem.getPaymentMethod() != null) {
				i.setPaymentMethod(updateItem.getPaymentMethod());
				isUpdateColumnExist = true;
			}
			// 利用金額、支払手数料、支払総額
			// ②「利用金額」～「支払総額」の要素がどれか1つ存在するなら全て存在する：BUS-20203
			BigDecimal ua = updateItem.getUsageAmount();
			BigDecimal fa = updateItem.getFeeAmount();
			BigDecimal ta = updateItem.getTotalAmount();
			if (ua != null && fa != null && ta != null) {
				// 全部あるなら保存
				i.setUsageAmount(ua);
				i.setFeeAmount(fa);
				i.setTotalAmount(ta);
				isUpdateColumnExist = true;
			} else if (ua != null || fa != null || ta != null) {
				// 全部はないのに少なくとも1つある場合はエラー
				throw new BusinessException(null, ErrorMessage.ITM_NOT_EXIST_ALL_AMOUNT,
						ErrorCode.ITM_BUS_ALL_AMOUNT_NOT_EXIST);
			}
			// 当月支払金額
			if (updateItem.getCurrentMonthPaid() != null) {
				i.setCurrentMonthPaid(updateItem.getCurrentMonthPaid());
				isUpdateColumnExist = true;
			}
			// 次月繰越残高
			if (updateItem.getNextMonthPaid() != null) {
				i.setNextMonthPaid(updateItem.getNextMonthPaid());
				isUpdateColumnExist = true;
			}
			// 新規サイン
			if (updateItem.getIsNewItem() != null) {
				i.setIsNewItem(updateItem.getIsNewItem());
				isUpdateColumnExist = true;
			}
			// カテゴリID
			Integer categoryId = updateItem.getCategoryId();
			if (categoryId != null) {
				// リクエストパラメータのカテゴリIDのPKを取得
				CategoryEntity unknownCategoryEntity = categoryRepository
						.findByIdAndMemberIdAndDeletedAtIsNull(categoryId, FixedMemberId.FIXED_MEMBER_ID)
						.orElseThrow(() ->
						// リクエストパラメータのカテゴリがなければエラー
						new BusinessException("categoryId", ErrorMessage.ITM_NOT_EXIST_CATEGORY_ID,
								ErrorCode.ITM_BUS_CATEGORY_ID_NOT_EXIST));
				// リクエストパラメータのカテゴリIDのPK
				long categoryPk = unknownCategoryEntity.getPk();
				i.setCategoryId(categoryPk);
				isUpdateColumnExist = true;
			}
			// メモ
			if (updateItem.getMemo() != null) {
				i.setMemo(updateItem.getMemo());
				isUpdateColumnExist = true;
			}
			// ①「利用日」～「メモ」までの要素が少なくとも1つが存在する：BUS-20202
			if (!isUpdateColumnExist) {
				throw new BusinessException(null, ErrorMessage.ITM_NOT_EXIST_UPDATE_COLUMN,
						ErrorCode.ITM_BUS_UPDATE_COLUMN_NOT_EXIST);
			}
			// 更新EntityListに追加
			uiel.add(i);
		}
		// 5.「年月」の「-」を消して「年月」を作成（#1）
		String targetYearMonthDB = targetYearMonth.format(YEAR_MONTH_DB_FORMATTER);
		// レスポンス用
		List<P202ResponseUpdateResult> updateResults = new ArrayList<P202ResponseUpdateResult>();
		// 6.以下の処理で各明細でDBのデータを更新
		// ①「DB02_明細」から「会員ID」一致と「PublicID」=「明細ID」、「支払い年月」=「年月」で検索し
		// 「明細」に含まれている更新データを反映
		// ※会員IDは固定のものを使用する
		for (ItemEntity i : uiel) {
			// 結果格納
			int result = 0;
			// DB登録
			try {
				// 各更新データをDBへ登録
				result = itemRepository.patchUpdate(FixedMemberId.FIXED_MEMBER_ID, i.getPublicId(), targetYearMonthDB,
						i.getUsageDate(), i.getTitle(), i.getPayer(), i.getPaymentMethod(), i.getUsageAmount(),
						i.getFeeAmount(), i.getTotalAmount(), i.getCurrentMonthPaid(), i.getNextMonthPaid(),
						i.getIsNewItem(), i.getCategoryId(), i.getMemo(), Instant.now(), FixedMemberId.FIXED_MEMBER_ID);
			} catch (DataIntegrityViolationException e) {
				// 送信に失敗したら送信前の状態にロールバックさせ、エラーを投げる
				if (SQLUtils.isUniqueViolation(e, UQ_ITEMS_CODE_ACTIVE)) {
					// DB 側で競合（23505）が起きた場合もクライアント向けに統一
					throw new DuplicateValueException("publicId", ErrorMessage.ITM_CONFLICT_PUBLIC_ID,
							ErrorCode.ITM_BUS_CONFLICT_PUBLIC_ID);
				} else if (SQLUtils.isForeignKeyViolation(e, SQLState.FOREIGN_KEY_VIOLATION.getCode())) {
					// FK違反の場合は会員IDかカテゴリIDが悪いことを通知
					throw new BusinessException("memberId or categoryId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
							ErrorCode.ITM_BUS_BAD_FOREIGN_KEY);

				}
				// 想定外の永続化エラーは自前の500用例外に正規化して再投げ
				throw new UnexpectedPersistenceException(null, ErrorMessage.COM_SERVER_ERROR_MESSAGE,
						ErrorCode.COM_SERVER_ERROR, e);
			}
			// ②更新件数として「1」が返ってきたら更新成功：レスポンス「結果」=true
			// 「0」が返ってきたら更新失敗：レスポンス「結果」=false メッセージ="NOT_FOUND"（#2）
			updateResults.add(toP202ResposneUpdateResult(i.getPublicId(), result));
		}
		return toP202Response(targetYearMonth.toString(), updateResults);
	}

	@Transactional
	public P203Response importCsv(P203Request request) {
		// 2. バリデーションチェック（必須・長さ・形式・範囲）
		// ①multipart/form-dataの「年月」を「-」で分割してバリデーションチェック（範囲）
		YearMonth targetYearMonth = YearMonth.parse(request.getYearMonth(), YEAR_MONTH_FORMATTER);
		CsvData csvData = CsvReader.validateAndParse(request.getItemFile(), targetYearMonth);
		// 3.DBへEntityに格納したデータを送信
		List<CsvRow> row = csvData.rows();
		List<CsvParseError> errors = csvData.errors();
		// ただし直前のチェックで1つでもエラーがあればDBへの取り込みを行わずレスポンス返却に移る
		if (errors.isEmpty()) {
			// 会員IDの存在チェック（FK制約違反回避のため事前にチェック）
			memberRepository.findByIdAndDeletedAtIsNull(FixedMemberId.FIXED_MEMBER_ID)
					.orElseThrow(() -> new BusinessException("memberId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
							ErrorCode.ITM_BUS_BAD_FOREIGN_KEY));
			// 未設定のカテゴリIDのPKを取得、なければ作成する
			CategoryEntity unknownCategoryEntity = categoryRepository
					.findByIdAndMemberIdAndDeletedAtIsNull(CategoryId.UNKNOWN, FixedMemberId.FIXED_MEMBER_ID)
					.orElseGet(() -> {
						// 未設定カテゴリがなければ作成
						return categoryRepository.save(getUnknownCategoryEntity(FixedMemberId.FIXED_MEMBER_ID));
					});
			// 未設定カテゴリのPKを取得
			long unknownCategoryId = unknownCategoryEntity.getPk();
			// CSVの各行データをEntityに変換しDBへ登録
			for (CsvRow csvRow : row) {
				// CSV行データをEntityに変換
				ItemEntity entity = toEntity(csvRow, unknownCategoryId, targetYearMonth, FixedMemberId.FIXED_MEMBER_ID);
				// DB登録
				try {
					// CSVの各行データをEntityに変換しDBへ登録
					itemRepository.save(entity);
				} catch (DataIntegrityViolationException e) {
					// 送信に失敗したら送信前の状態にロールバックさせ、エラーを投げる
					if (SQLUtils.isUniqueViolation(e, UQ_ITEMS_CODE_ACTIVE)) {
						// DB 側で競合（23505）が起きた場合もクライアント向けに統一
						throw new DuplicateValueException("publicId", ErrorMessage.ITM_CONFLICT_PUBLIC_ID,
								ErrorCode.ITM_BUS_CONFLICT_PUBLIC_ID);
					} else if (SQLUtils.isForeignKeyViolation(e, SQLState.FOREIGN_KEY_VIOLATION.getCode())) {
						// FK違反の場合は
						throw new BusinessException("memberId or categoryId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
								ErrorCode.ITM_BUS_BAD_FOREIGN_KEY);

					}
					// 想定外の永続化エラーは自前の500用例外に正規化して再投げ
					throw new UnexpectedPersistenceException(null, ErrorMessage.COM_SERVER_ERROR_MESSAGE,
							ErrorCode.COM_SERVER_ERROR, e);
				}
				// 永続化コンテキストをクリアし、メモリ使用量を抑制
				entityManager.flush();
				entityManager.clear();
			}
		}

		// 5.レスポンスを返却
		// 通常時：登録データを返却（traceId含む）
		// 上記レスポンス仕様の形に整形
		// エラー時：例外コード変換し共通フォーマットで応答
		// （3.④内で発生したエラーは成功レスポンス内で返却）
		return toP203Response(row.size(), errors.size(), errors);
	}

	// 未設定カテゴリのEntityを作成
	private CategoryEntity getUnknownCategoryEntity(String memberId) {
		CategoryEntity entity = new CategoryEntity();
		entity.setId(CategoryId.UNKNOWN);
		entity.setName("未設定");
		entity.setDisplayOrder(9999); // 未設定カテゴリは最後に表示
		entity.setMemberId(memberId);
		entity.setCreatedBy(memberId);
		entity.setUpdatedBy(memberId);
		return entity;
	}

	// CSV行データをEntityに変換
	private ItemEntity toEntity(CsvRow row, long categoryId, YearMonth yearMonth, String memberId) {
		ItemEntity entity = new ItemEntity();
		entity.setPublicId(UlidCreator.getUlid().toString());
		entity.setBillingYm(yearMonth.format(YEAR_MONTH_DB_FORMATTER));
		entity.setUsageDate(row.usageDate());
		entity.setTitle(row.title());
		entity.setPayer(row.payer());
		entity.setPaymentMethod(row.paymentMethod());
		entity.setUsageAmount(row.usageAmount());
		entity.setFeeAmount(row.feeAmount());
		entity.setTotalAmount(row.totalAmount());
		entity.setCurrentMonthPaid(row.currentMonthPaid());
		entity.setNextMonthPaid(row.nextMonthPaid());
		entity.setIsNewItem(row.isNewItems());
		entity.setCategoryId(categoryId);
		entity.setMemo(row.memo());

		entity.setMemberId(memberId); // TODO: 認証実装までは固定
		entity.setCreatedBy(memberId);
		entity.setUpdatedBy(memberId);

		return entity;
	}

	// 明細情報を一覧取得のレスポンスに変換
	private static P201Response toP201Response(
			String yearMonth, List<ItemOverViewWithCategoryDto> items, long totalAmount) {
		P201Response res = new P201Response();
		res.setYearMonth(yearMonth);
		res.setTotalNum(items.size());
		res.setTotalAmount(totalAmount);
		if (items.size() > 0) {
			res.setItemizedList(toP201ResponseItemizedList(items));
		}
		return res;
	}

	// 明細情報リストをレスポンスへ変換
	private static List<P201ResponseItem> toP201ResponseItemizedList(List<ItemOverViewWithCategoryDto> items) {
		List<P201ResponseItem> res = new ArrayList<P201ResponseItem>();
		for (ItemOverViewWithCategoryDto item : items) {
			res.add(toP201ResponseItem(item));
		}
		return res;
	}

	// 明細情報をレスポンスへ変換
	private static P201ResponseItem toP201ResponseItem(ItemOverViewWithCategoryDto item) {
		P201ResponseItem res = new P201ResponseItem();
		res.setItemId(item.publicId());
		res.setDate(item.usageDate().format(USAGE_DATE_FORMATTER));
		res.setTitle(item.title());
		res.setCategoryId(item.categoryLocalId());
		res.setMemo(item.memo());
		res.setAmount(item.usageAmount().longValue());
		return res;
	}

	// 明細更新結果をレスポンスへ変換
	private static P202Response toP202Response(String yearMonth, List<P202ResponseUpdateResult> list) {
		P202Response res = new P202Response();
		res.setYearMonth(yearMonth);
		res.setTotalNum(list.size());
		res.setUpdateResultList(list);
		return res;
	}

	// 明細更新結果の1件をレスポンスへ変換
	private static P202ResponseUpdateResult toP202ResposneUpdateResult(String itemId, int result) {
		P202ResponseUpdateResult res = new P202ResponseUpdateResult();
		res.setItemId(itemId);
		if (result == 1) {
			res.setStatus(true);
		} else {
			// 0を想定
			res.setStatus(false);
			res.setMessage(UPDATE_NOT_FOUND);
		}
		return res;
	}

	// 成功・失敗件数＋エラーリストをレスポンスに変換
	private static P203Response toP203Response(int success, int failure, List<CsvParseError> errors) {
		P203Response res = new P203Response();
		res.setTotal(success + failure); // 「成功件数」＋「失敗件数」=「読込件数」とする
		res.setSuccess(success);
		res.setFailed(failure);
		res.setTraceId(TraceIdHolder.get()); // トレースIDをセット
		// エラーリストが空ならそのまま返却
		if (errors == null || errors.isEmpty()) {
			return res;
		}
		// エラーリストをレスポンス用に変換してセット
		res.setErrors(toP203ResponseErrors(errors));
		return res;
	}

	// エラーリストをレスポンス用エラーリストに変換
	private static List<P203ResponseError> toP203ResponseErrors(List<CsvParseError> errors) {
		List<P203ResponseError> reslist = new ArrayList<P203ResponseError>();
		for (CsvParseError error : errors) {
			P203ResponseError resError = new P203ResponseError();
			resError.setLine(error.lineNumber());
			resError.setCode(error.code());
			resError.setMessage(error.errorMessage());
			reslist.add(resError);
		}
		return reslist;
	}
}
