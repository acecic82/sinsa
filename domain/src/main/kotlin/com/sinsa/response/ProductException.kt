package com.sinsa.response

import com.sinsa.response.enum.ExceptionCode

class ProductException(
    override val exceptionCode: ExceptionCode,
    override val message: String?
) : BusinessException(exceptionCode, message)
