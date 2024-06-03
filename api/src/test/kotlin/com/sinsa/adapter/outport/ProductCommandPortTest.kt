package com.sinsa.adapter.outport

import com.sinsa.entity.Product
import com.sinsa.entity.ProductEntity
import com.sinsa.repositories.ProductJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

internal class ProductCommandPortTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    init {
        val productJpaRepository = mockk<ProductJpaRepository>()
        val productCommandPort = ProductCommandPort(productJpaRepository)

        Given("Save 하는 경우") {
            When("Save 이후 세이브 된 정보를 return 했을 때") {
                val product = Product(null, "상의", "A", BigDecimal(100))
                val productEntity = ProductEntity.from(product)
                every { productJpaRepository.save(any()) } returns productEntity

                Then("Product 객체를 Return 해야한다.") {
                    val result = productCommandPort.save(product)

                    assertEquals(product.productId, result.productId)
                    assertEquals(product.category, result.category)
                    assertEquals(product.brand, result.brand)
                    assertEquals(product.price, result.price)
                }
            }
        }
    }
}