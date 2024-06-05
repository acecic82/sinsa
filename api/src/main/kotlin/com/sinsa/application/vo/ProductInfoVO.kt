package com.sinsa.application.vo

import com.sinsa.entity.Product
import java.math.BigDecimal

data class ProductInfoVO (
    val productId: Long?,
    val category: String,
    val brand: String,
    var price: BigDecimal
) {
    fun toProduct() = Product(
        this.productId,
        this.category,
        this.brand,
        this.price
    )
}
