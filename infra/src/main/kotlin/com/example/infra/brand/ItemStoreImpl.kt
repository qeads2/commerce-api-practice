package com.example.infra.brand

import com.example.domain.brand.ItemStore
import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Item
import com.example.infra.brand.exception.DataNotFound
import org.springframework.stereotype.Repository

@Repository
class ItemStoreImpl(
    private val itemRepository: ItemRepository,
) : ItemStore {
    override fun save(item: Item): Item {
        val entity =
            ItemEntity(
                id = item.id ?: 0L,
                brandId = item.brandId,
                category = item.category,
                price = item.price,
            )
        return itemRepository.save(entity).to()
    }

    override fun removeAll(items: List<Item>) {
        itemRepository.deleteAll(
            items.map { item ->
                ItemEntity(
                    id = item.id ?: 0L,
                    brandId = item.brandId,
                    category = item.category,
                    price = item.price,
                )
            },
        )
    }

    override fun findByBrandId(id: Long): List<Item> {
        return itemRepository.findByBrandId(id).map { it.to() }
    }

    override fun saveAll(items: List<Item>): List<Item> {
        val entities =
            items.map { item ->
                ItemEntity(
                    id = item.id ?: 0L,
                    brandId = item.brandId,
                    category = item.category,
                    price = item.price,
                )
            }
        return itemRepository.saveAll(entities).map { it.to() }
    }

    override fun getLowestPriceItems(): List<Item> {
        return itemRepository.findAllLowestPriceGroupByCategoryAndBrandId()
            .groupBy { it.category }
            .map {
                val lowPriceItem = it.value.minBy { item -> item.price }
                Item(
                    id = it.value.first().id,
                    brandId = lowPriceItem.brandId,
                    category = it.key,
                    price = lowPriceItem.price,
                )
            }
    }

    override fun getLowestPriceBrandSet(): List<Item> {
        val lowestPriceBrand =
            itemRepository.getLowestPriceBrandSet()
                ?: throw DataNotFound("가격이 가장 낮은 브랜드 정보가 없습니다.")
        return itemRepository.findByBrandId(lowestPriceBrand.brandId)
            .map { it.to() }
    }

    override fun findHighestPriceByCategory(category: Category): Item {
        return itemRepository.findTop1ByCategoryOrderByPriceDesc(category)?.to()
            ?: throw DataNotFound("해당 카테고리의 아이템 정보가 없습니다.")
    }

    override fun findLowestPriceByCategory(category: Category): Item {
        return itemRepository.findTop1ByCategoryOrderByPriceAsc(category)?.to()
            ?: throw DataNotFound("해당 카테고리의 아이템 정보가 없습니다")
    }
}
