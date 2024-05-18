package com.example.controller.dto

import com.example.controller.utils.NumberFormatter
import com.example.domain.brand.info.HighestLowestPriceBrandItem

data class HighestLowestBrandItemDTO(
    val 카테고리: String,
    val 최저가: List<BrandItemDTO>,
    val 최고가: List<BrandItemDTO>,
) {
    data class BrandItemDTO(
        val 브랜드: String,
        val 가격: String,
    )

    companion object {
        fun from(highestLowestPriceBrandItem: HighestLowestPriceBrandItem) =
            HighestLowestBrandItemDTO(
                카테고리 = highestLowestPriceBrandItem.category.desc,
                최저가 =
                    listOf(
                        BrandItemDTO(
                            브랜드 = highestLowestPriceBrandItem.lowestBrandItem.brandName,
                            가격 = NumberFormatter.comma(highestLowestPriceBrandItem.lowestBrandItem.price),
                        ),
                    ),
                최고가 =
                    listOf(
                        BrandItemDTO(
                            브랜드 = highestLowestPriceBrandItem.highestBrandItem.brandName,
                            가격 = NumberFormatter.comma(highestLowestPriceBrandItem.highestBrandItem.price),
                        ),
                    ),
            )
    }
}
