package com.sinsa.response

import com.sinsa.response.enum.ExceptionCode


sealed class BusinessException(
    open val exceptionCode: ExceptionCode,
    override val message: String?,
    override val cause: Throwable? = null
): RuntimeException(message, cause)