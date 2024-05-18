package com.example.infra.brand

import com.example.infra.brand.QItemEntity.itemEntity
import com.example.infra.brand.dto.LowestPriceCategoryItem
import com.example.infra.brand.dto.QLowestPriceCategoryItem
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ItemQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ItemQueryDslRepository {
    override fun findAllLowestPriceGroupByCategoryAndBrandId(): List<LowestPriceCategoryItem> {
        return queryFactory
            .select(
                QLowestPriceCategoryItem(
                    itemEntity.id.min(),
                    itemEntity.brandId,
                    itemEntity.category,
                    itemEntity.price.min(),
                ),
            )
            .from(itemEntity)
            .groupBy(itemEntity.category, itemEntity.brandId)
            .fetch()
    }
}
