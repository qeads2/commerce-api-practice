package com.example.infra.brand

import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<BrandEntity, Long> {
    fun findByName(name: String): BrandEntity?
}
