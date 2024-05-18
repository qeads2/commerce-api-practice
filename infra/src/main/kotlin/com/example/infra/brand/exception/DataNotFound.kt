package com.example.infra.brand.exception

import org.springframework.dao.DataAccessException

class DataNotFound(
    message: String,
    cause: Throwable? = null,
) : DataAccessException(message, cause)
