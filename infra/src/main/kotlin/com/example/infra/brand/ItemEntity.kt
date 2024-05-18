package com.example.infra.brand

import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Item
import jakarta.persistence.*

@Entity
@Table(
    name = "item",
    indexes = [
        Index(name = "ix_brand_id", columnList = "brand_id"),
        Index(name = "ix_category", columnList = "category"),
        Index(name = "ix_category_x_brand_id", columnList = "brand_id, category"),
    ],
)
class ItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "brand_id")
    val brandId: Long,
    @Enumerated(EnumType.STRING)
    var category: Category,
    var price: Int,
) : BaseEntity() {
    fun to() =
        Item(
            id = id,
            brandId = brandId,
            category = category,
            price = price,
        )
}
