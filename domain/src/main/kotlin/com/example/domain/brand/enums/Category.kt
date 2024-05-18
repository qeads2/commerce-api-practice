package com.example.domain.brand.enums

import com.example.domain.brand.exception.BadRequestException

enum class Category(
    val desc: String,
) {
    TOP("상의"),
    OUTER("아우터"),
    BOTTOM("하의"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리"),
    ;

    companion object {
        fun getByCategoryName(categoryName: String): Category {
            return entries.find { it.desc == categoryName }
                ?: throw BadRequestException("존재하지 않는 카테고리입니다.")
        }
    }
}
