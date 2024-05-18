package com.example.exception

import com.example.controller.dto.ResponseDTO
import com.example.domain.brand.exception.BadRequestException
import com.example.domain.brand.exception.NotFoundException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(exception: DataAccessException): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity(ResponseDTO.ofFailure(exception), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(exception: NotFoundException): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity(ResponseDTO.ofFailure(exception), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity(ResponseDTO.ofFailure(exception), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnExpectedException(exception: Exception): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity(ResponseDTO.ofFailure(exception), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun setDefaultBody(exception: Exception): MutableMap<String, Any?> {
        return mutableMapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "message" to exception.message,
            "stackTrace" to exception.stackTrace.joinToString("\n") { it.toString() },
        )
    }
}
