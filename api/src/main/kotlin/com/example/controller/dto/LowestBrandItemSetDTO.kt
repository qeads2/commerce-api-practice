package com.example.controller.dto

import com.example.controller.utils.NumberFormatter
import com.example.domain.brand.info.BrandItem

data class LowestBrandItemSetDTO(
    val 최저가: BrandItemDTO,
) {
    data class BrandItemDTO(
        val 브랜드: String,
        val 카테고리: List<CategoryWithItem>,
        val 총액: String,
    ) {
        data class CategoryWithItem(
            val 카테고리: String,
            val 가격: String,
        )
    }

    companion object {
        fun from(items: List<BrandItem>): LowestBrandItemSetDTO =
            LowestBrandItemSetDTO(
                최저가 =
                    BrandItemDTO(
                        브랜드 = items[0].brandName,
                        카테고리 =
                            items.map { item ->
                                BrandItemDTO.CategoryWithItem(
                                    카테고리 = item.category.desc,
                                    가격 = NumberFormatter.comma(item.price),
                                )
                            },
                        총액 =
                            NumberFormatter.comma(
                                items.sumOf { it.price },
                            ),
                    ),
            )
    }
}
