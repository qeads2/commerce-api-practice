package com.example.infra.brand.dto

import com.example.domain.brand.enums.Category
import com.querydsl.core.annotations.QueryProjection

data class LowestPriceCategoryItem
    @QueryProjection
    constructor(
        val id: Long,
        val brandId: Long,
        val category: Category,
        val price: Int,
    )
