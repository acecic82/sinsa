package com.sinsa.common.response

import com.sinsa.response.enum.ExceptionCode

data class ExceptionResponseDTO<T> (
    val code: ExceptionCode? = null,
    val content: T?
)