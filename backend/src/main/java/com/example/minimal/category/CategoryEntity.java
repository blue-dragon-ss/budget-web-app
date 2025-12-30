package com.example.minimal.category;

import java.time.Instant;

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

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
public class CategoryEntity {
	@Id
	@Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk")
	private Long pk; // カテゴリ主キー

	// 会員内カテゴリID（ローカルID）
	@Setter
	@Column(name = "id", nullable = false)
	private Integer id; // カテゴリローカルID

	@Setter
	@Column(name = "name", nullable = false, length = 20)
	private String name; // カテゴリ名

	@Setter
	@Column(name = "upper_id")
	private Long upperId; // 上位カテゴリID（自己参照）

	@Setter
	@Column(name = "display_order", nullable = false)
	private Integer displayOrder; // 表示優先順位

	@Setter
	@Column(name = "member_id", nullable = false, length = 26)
	private String memberId; // 会員ID(ULID)

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Setter
	@Column(name = "created_by", length = 26)
	private String createdBy;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Setter
	@Column(name = "updated_by", length = 26)
	private String updatedBy;

	@Column(name = "deleted_at")
	private Instant deletedAt;

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

	// --- 関連（JOINに使う） ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upper_id", referencedColumnName = "pk", insertable = false, updatable = false)
	private CategoryEntity upperCategory;

}
