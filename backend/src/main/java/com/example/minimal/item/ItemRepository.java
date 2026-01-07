package com.example.minimal.item;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.minimal.item.dto.ItemOverViewWithCategoryDto;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
	// 会員IDと請求年月でアクティブなアイテム一覧を取得（カテゴリ情報付き）
	@Query("""
			    select new com.example.minimal.item.dto.ItemOverViewWithCategoryDto(
			        i.id,
			        i.publicId,
			        i.billingYm,
			        i.usageDate,
			        i.title,
			        i.usageAmount,
			        i.memo,
			        c.id
			    )
			    from ItemEntity i
			    join i.category c
			    where i.memberId = :memberId
			      and i.billingYm = :billingYm
			      and i.deletedAt is null
			      and c.deletedAt is null
			      and c.memberId = :memberId
			    order by i.usageDate asc, i.id asc
			""")
	List<ItemOverViewWithCategoryDto> findActiveItemsOverViewWithCategory(
			@Param("memberId") String memberId, @Param("billingYm") String billingYm);

	@Query("""
			    select coalesce(sum(i.usageAmount), 0)
			    from ItemEntity i
			    where i.memberId = :memberId
			      and i.billingYm = :billingYm
			      and i.deletedAt is null
			""")
	BigDecimal sumUsageAmount(@Param("memberId") String memberId, @Param("billingYm") String billingYm);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update ItemEntity i
			   set i.usageDate = case when :usageDate is null then i.usageDate else :usageDate end,
			       i.title = case when :title is null then i.title else :title end,
			       i.payer = case when :payer is null then i.payer else :payer end,
			       i.paymentMethod = case when :paymentMethod is null then i.paymentMethod else :paymentMethod end,
			       i.usageAmount = case when :usageAmount is null then i.usageAmount else :usageAmount end,
			       i.feeAmount = case when :feeAmount is null then i.feeAmount else :feeAmount end,
			       i.totalAmount = case when :totalAmount is null then i.totalAmount else :totalAmount end,
			       i.currentMonthPaid = case when :currentMonthPaid is null then i.currentMonthPaid else :currentMonthPaid end,
			       i.nextMonthPaid = case when :nextMonthPaid is null then i.nextMonthPaid else :nextMonthPaid end,
			       i.isNewItem = case when :isNewItem is null then i.isNewItem else :isNewItem end,
			       i.categoryId = case when :categoryId is null then i.categoryId else :categoryId end,
			       i.memo = case when :memo is null then i.memo else :memo end,
			       i.updatedAt = :updatedAt,
			       i.updatedBy = :updatedBy
			 where i.publicId = :publicId
			   and i.memberId = :memberId
			   and i.billingYm = :billingYm
			   and i.deletedAt is null
			""")
	int patchUpdate(
			@Param("memberId") String memberId, @Param("publicId") String publicId,
			@Param("billingYm") String billingYm,

			@Param("usageDate") LocalDate usageDate, @Param("title") String title, @Param("payer") String payer,
			@Param("paymentMethod") String paymentMethod,

			@Param("usageAmount") BigDecimal usageAmount, @Param("feeAmount") BigDecimal feeAmount,
			@Param("totalAmount") BigDecimal totalAmount, @Param("currentMonthPaid") BigDecimal currentMonthPaid,
			@Param("nextMonthPaid") BigDecimal nextMonthPaid,

			@Param("isNewItem") Boolean isNewItem, @Param("categoryId") Long categoryId, @Param("memo") String memo,

			@Param("updatedAt") Instant updatedAt, @Param("updatedBy") String updatedBy);
}
