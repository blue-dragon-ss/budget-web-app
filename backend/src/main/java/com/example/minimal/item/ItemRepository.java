package com.example.minimal.item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.minimal.item.dto.ItemOverViewWithCategoryDto;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
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
}
