package com.example.domain.brand.info

import com.example.domain.brand.enums.Category

data class ModifyBrandItem(
    val brandId: Long?,
    val brandName: String?,
    val items: List<BrandItem>,
) {
    data class BrandItem(
        val id: Long?,
        val category: Category,
        val price: Int,
    ) {
        fun toItem(brandId: Long) =
            Item(
                id = id,
                brandId = brandId,
                category = category,
                price = price,
            )
    }

    fun toBrand() =
        Brand(
            id = brandId!!,
            name = brandName!!,
        )
}
