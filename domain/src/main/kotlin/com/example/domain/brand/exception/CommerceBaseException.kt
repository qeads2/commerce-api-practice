package com.example.domain.brand.exception

open class CommerceBaseException(
    val code: Int,
    override val message: String,
) : Exception()
