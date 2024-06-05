package com.sinsa.entity

import jakarta.persistence.*
import java.math.BigDecimal


//product 와 차이점은 brand & category 로 봤을 때 항상 minValue 를 저장하고 있습니다.
//product 는 brand & category 를 기준으로 다수의 상품이 존재할 수 있습니다.
//왜 이 테이블이 필요한지 설명하자면 2번 기능을 좀 더 빠르게 리턴하기 위해 선택했습니다.
//비지니스의 특성상 write 보단 read 가 많을 것이고, read 를 빠르게 해내는것이 중요합니다.
//2번 api 비지니스를 수행하기 위해서 productTable 에서 복잡하게 연산하지 않고 이 테이블 하나를 추가해
//빠르게 비지니스를 해결하는것이 더 적합하다고 생각하여 급하게 개선하였습니다.
//한 가지 걸리는 점은 이렇게 되면 product 추가 시 min_product, product 2개의 테이블을 업데이트 해야하며
//싱크를 맞춰야한다는 문제가 있긴 하지만 read 가 훨씬 더 많을 것이기 때문에 과감하게 디비를 하나 더 늘리는 방향으로
//도전해봤습니다.
@Entity
@Table(name = "min_product")
data class MinProductEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val category: String,
    val brand: String,
    val price: BigDecimal
) {
    constructor() : this(null, "", "", BigDecimal.ZERO)

    companion object {
        fun from(product: Product) = MinProductEntity(
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
