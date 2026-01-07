package com.example.minimal.item;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.minimal.category.CategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 明細テーブル

@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor
public class ItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	@Column(name = "id", nullable = false)
	private Long id;

	@Setter
	@Column(name = "public_id", length = 26, nullable = false)
	private String publicId; // ULID(26)

	@Setter
	@Column(name = "billing_ym", length = 6, nullable = false)
	private String billingYm; // 支払年月(YYYYMM)

	@Setter
	@Column(name = "usage_date", nullable = false)
	private LocalDate usageDate; // 利用日

	@Setter
	@Column(name = "title", length = 100, nullable = false)
	private String title; // 明細タイトル

	@Setter
	@Column(name = "payer", length = 100, nullable = false)
	private String payer; // 利用者

	@Setter
	@Column(name = "payment_method", length = 50, nullable = false)
	private String paymentMethod; // 支払方法

	@Setter
	@Column(name = "usage_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal usageAmount; // 利用金額(円)

	@Setter
	@Column(name = "fee_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal feeAmount; // 支払手数料(円)

	@Setter
	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount; // 支払総額(円)

	@Setter
	@Column(name = "current_month_paid", nullable = false, precision = 10, scale = 2)
	private BigDecimal currentMonthPaid; // 当月支払金額(円)

	@Setter
	@Column(name = "next_month_paid", nullable = false, precision = 10, scale = 2)
	private BigDecimal nextMonthPaid; // 次月繰越金額(円)

	@Setter
	@Column(name = "is_new_item", nullable = false)
	private Boolean isNewItem; // 新規

	@Setter
	@Column(name = "category_id", nullable = false)
	private Long categoryId; // カテゴリID

	@Setter
	@Column(name = "memo")
	private String memo; // メモ

	@Setter
	@Column(name = "member_id", nullable = false, length = 26)
	private String memberId; // 会員ID(ULID-26)

	@Column(name = "created_at", nullable = false)
	private Instant createdAt; // 作成日時

	@Setter
	@Column(name = "created_by", length = 26)
	private String createdBy; // 作成者

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt; // 更新日時

	@Setter
	@Column(name = "updated_by", length = 26)
	private String updatedBy; // 更新者

	@Column(name = "deleted_at")
	private Instant deletedAt; // 論理削除日時

	@PrePersist
	public void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = Instant.now();
	}

	// --- JOIN用の関連（items.category_id -> categories.pk） ---
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", referencedColumnName = "pk", insertable = false, updatable = false)
	private CategoryEntity category;
}
