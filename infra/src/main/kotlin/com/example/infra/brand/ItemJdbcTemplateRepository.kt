package com.example.infra.brand

import com.example.infra.brand.dto.LowestPriceBrandSetDTO

interface ItemJdbcTemplateRepository {
    fun getLowestPriceBrandSet(): LowestPriceBrandSetDTO?
}
