package com.example.domain.brand

import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Item

interface ItemStore {
    fun save(item: Item): Item

    fun removeAll(items: List<Item>)

    fun findByBrandId(id: Long): List<Item>

    fun saveAll(items: List<Item>): List<Item>

    fun getLowestPriceItems(): List<Item>

    fun getLowestPriceBrandSet(): List<Item>

    fun findHighestPriceByCategory(category: Category): Item

    fun findLowestPriceByCategory(category: Category): Item
}
