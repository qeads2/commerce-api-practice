package com.example.domain.brand.exception

class BadRequestException(
    override val message: String,
) : CommerceBaseException(400, message)
