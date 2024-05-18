package com.example.domain.brand.exception

class NotFoundException(
    override val message: String,
) : CommerceBaseException(404, message)
