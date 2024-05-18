package com.example.domain.brand.info

import com.example.domain.brand.enums.Category

data class Item(
    val id: Long?,
    val brandId: Long,
    val category: Category,
    val price: Int,
) {
    fun toBrandItem(brandName: String) =
        BrandItem(
            id = id,
            brandName = brandName,
            category = category,
            price = price,
        )
}
