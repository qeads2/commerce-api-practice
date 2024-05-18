package com.example.infra.brand

import com.example.infra.brand.dto.LowestPriceCategoryItem

interface ItemQueryDslRepository {
    fun findAllLowestPriceGroupByCategoryAndBrandId(): List<LowestPriceCategoryItem>
}
