package com.example.domain.brand

import com.example.domain.brand.info.Brand

interface BrandStore {
    fun findById(id: Long): Brand

    fun findByIds(ids: List<Long>): List<Brand>

    fun findByName(name: String): Brand?

    fun save(brand: Brand): Brand

    fun save(name: String): Brand

    fun delete(brand: Brand)
}
