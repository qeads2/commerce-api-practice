package com.example.infra.brand

import com.example.domain.brand.info.Brand
import jakarta.persistence.*

@Entity
@Table(name = "brand")
class BrandEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
) : BaseEntity() {
    fun to(): Brand {
        return Brand(
            id = id,
            name = name,
        )
    }
}
