package com.sinsa.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

@Entity
@Table(name = "product")
data class ProductEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull
    val category: String,
    @NotNull
    val brand: String,
    @NotNull
    val price: BigDecimal
) {
    constructor() : this(null, "", "", BigDecimal.ZERO)

    companion object {
        fun from(product: Product) = ProductEntity(
            id = product.productId,
            category = product.category,
            brand = product.brand,
            price = product.price
        )
    }

    fun toProduct() = Product(
        this.id,
        this.category,
        this.brand,
        this.price
    )
}
