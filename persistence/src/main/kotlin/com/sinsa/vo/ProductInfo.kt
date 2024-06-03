package com.sinsa.vo

import java.math.BigDecimal

data class ProductInfo(
    val id: Long,
    val category: String,
    val brand: String,
    val price: BigDecimal
)
