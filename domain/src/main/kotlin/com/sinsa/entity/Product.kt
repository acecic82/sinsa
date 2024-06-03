package com.sinsa.entity

import java.math.BigDecimal

data class Product (
    var productId: Long? = null,
    var category: String,
    var brand: String,
    var price: BigDecimal
) {
    fun isSame(product: Product): Boolean {
        return this.category == product.category && this.brand == product.brand && this.price == product.price
    }

    fun update(product: Product) {
        category = product.category
        brand = product.brand
        price = product.price
    }

    companion object {
        const val BRAND_SUM_PRICE_LIMIT = 10L
        const val ORDER_PRICE_LIMIT = 10L
    }
}
