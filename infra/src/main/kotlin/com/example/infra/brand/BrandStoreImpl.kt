package com.example.infra.brand

import com.example.domain.brand.BrandStore
import com.example.domain.brand.info.Brand
import com.example.infra.brand.exception.DataNotFound
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class BrandStoreImpl(
    private val brandRepository: BrandRepository,
) : BrandStore {
    override fun findById(id: Long): Brand {
        return brandRepository.findByIdOrNull(id)?.to() ?: throw DataNotFound("브랜드가 정보가 없습니다.")
    }

    override fun findByIds(ids: List<Long>): List<Brand> {
        return brandRepository.findAllById(ids).map { it.to() }
    }

    override fun findByName(name: String): Brand? {
        return brandRepository.findByName(name)?.to()
    }

    override fun save(brand: Brand): Brand {
        val entity =
            BrandEntity(
                id = brand.id,
                name = brand.name,
            )
        return brandRepository.save(entity).to()
    }

    override fun save(name: String): Brand {
        val entity = BrandEntity(name = name)
        return brandRepository.save(entity).to()
    }

    override fun delete(brand: Brand) {
        val entity =
            BrandEntity(
                id = brand.id,
                name = brand.name,
            )
        brandRepository.delete(entity)
    }
}
