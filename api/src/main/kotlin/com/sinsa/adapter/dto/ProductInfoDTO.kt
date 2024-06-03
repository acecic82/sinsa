package com.sinsa.adapter.dto

import com.sinsa.application.vo.ProductInfoVO
import java.math.BigDecimal

data class ProductInfoDTO (
    val productId: Long? = null,
    val category: String,
    val brand: String,
    val price: BigDecimal
) {
    fun toVO() = ProductInfoVO(productId, category, brand, price)
}