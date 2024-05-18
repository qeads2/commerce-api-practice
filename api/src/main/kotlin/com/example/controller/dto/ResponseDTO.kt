package com.example.controller.dto

import java.time.LocalDateTime

data class ResponseDTO<T>(
    val success: Boolean,
    val error: Error? = null,
    val data: T?,
) {
    data class Error(
        val message: String?,
        val stackTrace: String,
        val timestamp: String,
    ) {
        companion object {
            fun of(exception: Exception): Error {
                return Error(
                    message = exception.message,
                    stackTrace = exception.stackTraceToString().substring(0..200),
                    timestamp = LocalDateTime.now().toString(),
                )
            }
        }
    }

    companion object {
        fun <T> ofSuccess(data: T?): ResponseDTO<T> {
            return ResponseDTO(
                success = true,
                data = data,
            )
        }

        fun <T> ofFailure(exception: Exception): ResponseDTO<T> {
            return ResponseDTO(
                success = false,
                error = Error.of(exception),
                data = null,
            )
        }
    }
}
