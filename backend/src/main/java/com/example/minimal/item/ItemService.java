package com.example.minimal.item;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minimal.category.CategoryEntity;
import com.example.minimal.category.CategoryRepository;
import com.example.minimal.common.TraceIdHolder;
import com.example.minimal.common.constants.CategoryId;
import com.example.minimal.common.constants.SQLState;
import com.example.minimal.common.exception.DuplicateValueException;
import com.example.minimal.common.exception.UnexpectedPersistenceException;
import com.example.minimal.common.exception.error.BusinessException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.common.util.CsvReader;
import com.example.minimal.common.util.CsvReader.CsvData;
import com.example.minimal.common.util.CsvReader.CsvParseError;
import com.example.minimal.common.util.CsvReader.CsvRow;
import com.example.minimal.common.util.SQLUtils;
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

	private final String FIXED_MEMBER_ID = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // TODO: 認証実装までは固定

	@PersistenceContext
	private EntityManager entityManager;

	public ItemService(
			ItemRepository itemRepository, CategoryRepository categoryRepository, MemberRepository memberRepository) {
		this.itemRepository = itemRepository;
		this.categoryRepository = categoryRepository;
		this.memberRepository = memberRepository;
	}

	private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter YEAR_MONTH_DB_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

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
			memberRepository.findByIdAndDeletedAtIsNull(FIXED_MEMBER_ID)
					.orElseThrow(() -> new BusinessException("memberId", ErrorMessage.ITM_BAD_FOREIGN_KEY,
							ErrorCode.ITM_BUS_BAD_FOREIGN_KEY));
			// 未設定のカテゴリIDのPKを取得、なければ作成する
			CategoryEntity unknownCategoryEntity = categoryRepository
					.findByIdAndMemberIdAndDeletedAtIsNull(CategoryId.UNKNOWN, FIXED_MEMBER_ID).orElseGet(() -> {
						// 未設定カテゴリがなければ作成
						return categoryRepository.save(getUnknownCategoryEntity(FIXED_MEMBER_ID));
					});
			// 未設定カテゴリのPKを取得
			long unknownCategoryId = unknownCategoryEntity.getPk();
			// CSVの各行データをEntityに変換しDBへ登録
			for (CsvRow csvRow : row) {
				// CSV行データをEntityに変換
				ItemEntity entity = toEntity(csvRow, unknownCategoryId, targetYearMonth, FIXED_MEMBER_ID);
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
		entity.setNewItem(row.isNewItems());
		entity.setCategoryId(categoryId); // カテゴリはCSVに含まれないため初期値として0固定
		entity.setMemo(row.memo());

		entity.setMemberId(memberId); // TODO: 認証実装までは固定
		entity.setCreatedBy(memberId);
		entity.setUpdatedBy(memberId);

		return entity;
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
