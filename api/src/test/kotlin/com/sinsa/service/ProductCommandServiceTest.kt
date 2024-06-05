package com.sinsa.service

import com.sinsa.application.outport.DeleteProductPort
import com.sinsa.application.outport.FindBrandPort
import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.outport.SaveProductPort
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import com.sinsa.response.ProductException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

internal class ProductCommandServiceTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    init {
        val findProductPort = mockk<FindProductPort>()
        val deleteProductPort = mockk<DeleteProductPort>()
        val saveProductPort = mockk<SaveProductPort>()
        val findBrandPort = mockk<FindBrandPort>()

        val productCommandService =
            ProductCommandService(findProductPort, deleteProductPort, saveProductPort, findBrandPort)

        Given("Delete 를 수행하는 경우") {
            val productVO = ProductInfoVO(1L, "상의", "A", BigDecimal(100))
            val nullProductVO = ProductInfoVO(null, "", "", BigDecimal.ZERO)

            When("category, brand 로 조회한 결과가 없을 때") {
                every { findProductPort.findProductId(any(), any(), null) } returns listOf()

                Then("삭제할 대상이 없는 경우 Product exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(nullProductVO)
                    }
                }
            }

            When("category, brand 로 조회한 결과가 1개만 있는 경우") {
                every { findProductPort.findProductId(any(), any(), null) } returns listOf(1L)

                Then("삭제할 대상이 1개인 경우 Product exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(nullProductVO)
                    }
                }
            }

            When("id로 찾은 결과가 없을 때") {
                every { findProductPort.findProductId(any(), any(), null) } returns listOf(1L, 2L)
                every { findProductPort.findById(any()) } returns null

                Then("삭제할 대상이 없는 경우 Product Not found exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(productVO)
                    }
                }
            }

            When("id로 찾은 삭제 대상이 있는 경우") {
                val product = Product(1L, "", "", BigDecimal.ZERO)

                every { findProductPort.findProductId(any(), any(), null) } returns listOf(1L, 2L)
                every { findProductPort.findById(any()) } returns product

                Then("삭제 완료 후 true 를 return 한다.") {
                    every { deleteProductPort.delete(any()) } returns mockk()

                    val result = productCommandService.delete(productVO)

                    assertTrue(result)
                }
            }

            When("category, brand, price 로 찾은 데이터가 있어서 삭제할 때") {

                every { findProductPort.findProductId(any(), any(), null) } returns listOf(1L, 2L)
                every { findProductPort.findProductId(any(), any(), any()) } returns listOf(1L, 2L)

                Then("삭제 완료 후 true 를 return 한다.") {
                    every { deleteProductPort.delete(any()) } returns mockk()

                    val result = productCommandService.delete(nullProductVO)

                    assertTrue(result)
                }
            }
        }

        Given("Update 하는 경우") {
            val productVO = ProductInfoVO(1L, "상의", "A", BigDecimal(100))
            val nullProductVO = ProductInfoVO(null, "", "", BigDecimal.ZERO)

            When("들어온 정보에 id 정보가 없는 경우") {
                Then("update 할 대상이 없는 경우 ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(nullProductVO)
                    }
                }
            }

            When("들어온 정보의 id로 조회한 정보가 없을 때") {
                every { findProductPort.findById(any()) } returns null

                Then("update 할 대상이 없는 경우 ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
                }
            }

            When("들어온 정보의 id로 조회한 정보가 없을 때") {
                every { findProductPort.findById(any()) } returns null

                Then("update 할 대상이 없는 경우 ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
                }
            }

            When("들어온 정보의 id로 조회한 정보가 있지만, 업데이트한 결과와 업데이트될 정보가 일치하지 않는 경우") {
                val product = Product(1L, "", "", BigDecimal.ZERO)
                val updatedProduct = Product(1L, "상의2", "A", BigDecimal(100))

                every { findProductPort.findById(any()) } returns product
                every { saveProductPort.save(any()) } returns updatedProduct

                Then("ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
                }
            }

            When("Update 가 성공적으로 된 경우") {
                val product = Product(1L, "", "", BigDecimal.ZERO)
                val updatedProduct = Product(1L, "상의", "A", BigDecimal(100))

                every { findProductPort.findById(any()) } returns product
                every { saveProductPort.save(any()) } returns updatedProduct

                Then("True return") {
                    val result = productCommandService.update(productVO)

                    assertTrue(result)
                }
            }
        }

        Given("Save 를 수행하는 경우") {
            val productVO = ProductInfoVO(1L, "상의", "A", BigDecimal(100))

            When("brand 가 존재하지 않을 때") {

                every { findBrandPort.findExistBrand(any()) } returns null

                Then("ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.save(productVO)
                    }
                }
            }

            When("minProduct 가 존재하지 않는 경우") {
                every { findBrandPort.findExistBrand(any()) } returns "A"

                every { findProductPort.findMinProduct(any(), any()) } returns null

                Then("ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.save(productVO)
                    }
                }
            }

            When("저장한 데이터와 원본 데이터가 다른 경우") {
                val updatedProduct = Product(1L, "상의2", "A", BigDecimal(100))

                every { findBrandPort.findExistBrand(any()) } returns "A"
                every { findProductPort.findMinProduct(any(), any()) } returns productVO
                every { saveProductPort.save(any()) } returns updatedProduct

                Then("ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.save(productVO)
                    }
                }
            }

            When("저장한 데이터와 원본 데이터가 일치하는 경우") {
                val updatedProduct = Product(1L, "상의", "A", BigDecimal(100))

                every { findBrandPort.findExistBrand(any()) } returns "A"
                every { findProductPort.findMinProduct(any(), any()) } returns productVO
                every { saveProductPort.save(any()) } returns updatedProduct

                Then("true return") {
                    val result = productCommandService.save(productVO)

                    assertTrue(result)
                }
            }
        }
    }
}