package com.sinsa.common.response

import com.sinsa.response.enum.ExceptionCode

data class ResponseDTO<T>(
    val content: T? = null,
    val code: ExceptionCode? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(content: T? = null): ResponseDTO<T> {
            return ResponseDTO(content = content)
        }

        fun <T> fail(code: ExceptionCode?, message: String?): ResponseDTO<T> {
            return ResponseDTO(content = null, code = code, message = message)
        }
    }
}
