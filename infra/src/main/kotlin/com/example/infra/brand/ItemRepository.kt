package com.example.infra.brand

import com.example.domain.brand.enums.Category
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<ItemEntity, Int>, ItemQueryDslRepository, ItemJdbcTemplateRepository {
    fun findByBrandId(id: Long): List<ItemEntity>

    fun findTop1ByCategoryOrderByPriceDesc(category: Category): ItemEntity?

    fun findTop1ByCategoryOrderByPriceAsc(category: Category): ItemEntity?
}
