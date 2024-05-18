package com.example.domain.brand.info

import com.example.domain.brand.enums.Category

data class BrandItem(
    val id: Long?,
    val brandName: String,
    val category: Category,
    val price: Int,
)
