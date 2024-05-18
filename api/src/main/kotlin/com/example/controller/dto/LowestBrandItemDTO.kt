package com.example.controller.dto

import com.example.controller.utils.NumberFormatter
import com.example.domain.brand.info.BrandItem

data class LowestBrandItemDTO(
    val items: List<BrandItemDTO>,
    val totalPrice: String,
) {
    data class BrandItemDTO(
        val brandName: String,
        val category: String,
        val price: String,
    )

    companion object {
        fun from(items: List<BrandItem>): LowestBrandItemDTO =
            LowestBrandItemDTO(
                items =
                    items.map { item ->
                        BrandItemDTO(
                            brandName = item.brandName,
                            category = item.category.desc,
                            price = NumberFormatter.comma(item.price),
                        )
                    },
                totalPrice = NumberFormatter.comma(items.sumOf { it.price }),
            )
    }
}
