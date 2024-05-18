package com.example.infra.brand

import com.example.infra.brand.dto.LowestPriceBrandSetDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class ItemJdbcTemplateRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : ItemJdbcTemplateRepository {
    override fun getLowestPriceBrandSet(): LowestPriceBrandSetDTO? {
        val sql =
            """
            SELECT brand_id, SUM(min_price) AS total_min_price
            FROM (
                SELECT brand_id, category, MIN(price) AS min_price
                FROM item
                GROUP BY brand_id, category
            ) AS BrandCategoryMinPrices
            GROUP BY brand_id
            ORDER BY total_min_price ASC
            LIMIT 1
            """.trimIndent()

        return jdbcTemplate.queryForObject(
            sql,
            RowMapper { rs, _ ->
                LowestPriceBrandSetDTO(
                    brandId = rs.getLong("brand_id"),
                    totalPrice = rs.getInt("total_min_price"),
                )
            },
        )
    }
}
