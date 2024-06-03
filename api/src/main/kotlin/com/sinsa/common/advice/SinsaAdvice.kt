package com.sinsa.common.advice

import com.sinsa.common.response.ExceptionResponseDTO
import com.sinsa.response.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SinsaAdvice {
    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(HttpStatus.OK)
    fun exceptionHandler(e: BusinessException): ExceptionResponseDTO<String> {
        return ExceptionResponseDTO(e.exceptionCode, e.message)
    }

    @ExceptionHandler(Exception::class)
    fun exceptionHandler(e: Exception): ExceptionResponseDTO<String> {
        return ExceptionResponseDTO(content = e.message)
    }
}