package com.example.domain.brand.info

import com.example.domain.brand.enums.Category

data class HighestLowestPriceBrandItem(
    val category: Category,
    val highestBrandItem: BrandItem,
    val lowestBrandItem: BrandItem,
) {
    companion object {
        fun from(
            category: Category,
            highestBrandItem: BrandItem,
            lowestBrandItem: BrandItem,
        ) = HighestLowestPriceBrandItem(
            category = category,
            highestBrandItem = highestBrandItem,
            lowestBrandItem = lowestBrandItem,
        )
    }
}
