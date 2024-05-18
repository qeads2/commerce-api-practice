package com.example.facade

import com.example.domain.brand.CommerceService
import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Brand
import com.example.domain.brand.info.BrandItem
import com.example.domain.brand.info.HighestLowestPriceBrandItem
import com.example.domain.brand.info.ModifyBrandItem
import org.springframework.stereotype.Service

@Service
class CommerceFacade(
    private val commerceService: CommerceService,
) {
    fun modifyBrandItem(modifyBrandItem: ModifyBrandItem): Brand {
        return commerceService.modifyBrandItem(modifyBrandItem)
    }

    fun getLowestPriceItems(): List<BrandItem> {
        return commerceService.getLowestPriceItem()
    }

    fun getLowestPriceBrandSet(): List<BrandItem> {
        return commerceService.getLowestPriceBrandSet()
    }

    fun getBrandPrice(category: Category): HighestLowestPriceBrandItem {
        return commerceService.getBrandPrice(category)
    }
}
