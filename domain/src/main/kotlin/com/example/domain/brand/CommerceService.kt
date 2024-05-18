package com.example.domain.brand

import com.example.domain.brand.enums.Category
import com.example.domain.brand.exception.BadRequestException
import com.example.domain.brand.exception.NotFoundException
import com.example.domain.brand.info.Brand
import com.example.domain.brand.info.BrandItem
import com.example.domain.brand.info.HighestLowestPriceBrandItem
import com.example.domain.brand.info.ModifyBrandItem
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommerceService(
    private val brandStore: BrandStore,
    private val itemStore: ItemStore,
) {
    @Transactional
    fun modifyBrandItem(modifyBrandItem: ModifyBrandItem): Brand {
        return when {
            // brand create
            modifyBrandItem.brandId == null -> {
                if (modifyBrandItem.brandName == null) throw BadRequestException("브랜드명이 필요합니다.")
                val created = brandStore.save(modifyBrandItem.brandName)
                itemStore.saveAll(modifyBrandItem.items.map { it.toItem(created.id) })
                created
            }
            // brand update
            modifyBrandItem.brandName != null -> {
                var brand = brandStore.findById(modifyBrandItem.brandId)
                if (brand.name != modifyBrandItem.brandName) {
                    brand = brandStore.save(modifyBrandItem.toBrand())
                }
                processItems(brand.id, modifyBrandItem.items)
                brand
            }
            // brand delete
            else -> {
                val brand = brandStore.findById(modifyBrandItem.brandId)
                val items = itemStore.findByBrandId(brand.id)
                brandStore.delete(brand)
                itemStore.removeAll(items)
                brand
            }
        }
    }

    fun getLowestPriceItem(): List<BrandItem> {
        val lowestPriceBrandItems = itemStore.getLowestPriceItems()
        val brands = brandStore.findByIds(lowestPriceBrandItems.map { it.brandId })
        return lowestPriceBrandItems.map { item ->
            val brand = brands.find { it.id == item.brandId } ?: throw NotFoundException("브랜드 정보가 없습니다.")
            item.toBrandItem(brand.name)
        }
    }

    fun getLowestPriceBrandSet(): List<BrandItem> {
        val items = itemStore.getLowestPriceBrandSet()
        val brand = brandStore.findById(items.first().brandId)
        return items.map { it.toBrandItem(brand.name) }
    }

    fun getBrandPrice(category: Category): HighestLowestPriceBrandItem {
        val highestPriceItem = itemStore.findHighestPriceByCategory(category)
        val brand = brandStore.findById(highestPriceItem.brandId)
        val highestBrandItem = highestPriceItem.toBrandItem(brand.name)

        val lowestPriceItem = itemStore.findLowestPriceByCategory(category)
        val lowestBrand = brandStore.findById(lowestPriceItem.brandId)
        val lowestBrandItem = lowestPriceItem.toBrandItem(lowestBrand.name)

        return HighestLowestPriceBrandItem.from(category, highestBrandItem, lowestBrandItem)
    }

    private fun processItems(
        brandId: Long,
        items: List<ModifyBrandItem.BrandItem>,
    ) {
        val originalItems = itemStore.findByBrandId(brandId)
        val toBeCreatedItems = items.filter { it.id == null }
        val toBeUpdatedItems = items.filter { it.id != null }
        val toBeDeletedItems =
            originalItems.filter { originalItem ->
                items.none { it.id == originalItem.id }
            }

        toBeCreatedItems
            .map { it.toItem(brandId) }
            .let { itemStore.saveAll(it) }

        toBeUpdatedItems
            .map { toBeUpdatedItem ->
                val item =
                    originalItems.find { toBeUpdatedItem.id == it.id } ?: throw NotFoundException(
                        "아이템이 존재하지 않습니다. ${toBeUpdatedItem.id}",
                    )
                item.copy(
                    category = toBeUpdatedItem.category,
                    price = toBeUpdatedItem.price,
                )
            }
            .let { itemStore.saveAll(it) }
        toBeDeletedItems
            .let { itemStore.removeAll(it) }

        if (items.isEmpty()) {
            itemStore.removeAll(originalItems)
        }
    }
}
