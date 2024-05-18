package com.example.infra.brand.dto

import com.querydsl.core.annotations.QueryProjection

data class HighestLowestPriceItem
    @QueryProjection
    constructor(
        val highestBrandId: Long,
        val highestPrice: Int,
        val lowestBrandId: Long,
        val lowestPrice: Int,
    )
