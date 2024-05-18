package com.example.controller.dto

import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.ModifyBrandItem

data class ModifyBrandItemDTO(
    val brandId: Long?,
    val brandName: String?,
    val items: List<Item>,
) {
    data class Item(
        val id: Long?,
        val category: Category,
        val price: Int,
    )

    fun to(): ModifyBrandItem {
        return ModifyBrandItem(
            brandId = brandId,
            brandName = brandName,
            items =
                items.map { item ->
                    ModifyBrandItem.BrandItem(
                        id = item.id,
                        category = item.category,
                        price = item.price,
                    )
                },
        )
    }
}
