package com.sinsa.service

import com.sinsa.application.outport.*
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
        val saveBrandPort = mockk<SaveBrandPort>()

        val productCommandService =
            ProductCommandService(findProductPort, deleteProductPort, saveProductPort, findBrandPort, saveBrandPort)

        Given("Delete 를 수행하는 경우") {
            val productVO = ProductInfoVO(1L, "상의", "A", BigDecimal(100))
            val nullProductVO = ProductInfoVO(null, "", "", BigDecimal.ZERO)

            When("category, brand 로 조회한 결과가 없을 때") {

                Then("삭제할 대상이 없는 경우 Product exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(nullProductVO)
                    }
                }
            }

            When("category, brand 로 조회한 결과가 없을 때") {
                every { findProductPort.findProductByCategoryAndBrand(any(), any()) } returns listOf()

                Then("삭제할 대상이 없는 경우 Product exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(productVO)
                    }
                }
            }

            When("category, brand 로 조회한 결과가 1개만 있는 경우") {
                every { findProductPort.findProductByCategoryAndBrand(any(), any()) } returns listOf(productVO)

                Then("삭제할 대상이 1개인 경우 Product exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(nullProductVO)
                    }
                }
            }

            When("id로 찾은 결과가 없을 때") {
                every { findProductPort.findProductByCategoryAndBrand(any(), any()) } returns listOf(productVO, productVO)
                every { findProductPort.findMinProduct(any(), any()) } returns productVO
                every { findProductPort.findById(any()) } returns null

                Then("삭제할 대상이 없는 경우 Product Not found exception 발생") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(productVO)
                    }
                }
            }

            When("id로 찾은 삭제 대상이 있지만 minProduct 가 없는 경우") {
                val product = Product(1L, "", "", BigDecimal.ZERO)

                every { findProductPort.findProductByCategoryAndBrand(any(), any()) } returns listOf(productVO,productVO)
                every { findProductPort.findMinProduct(any(), any()) } returns null
                every { findProductPort.findById(any()) } returns product

                Then("Product Excep이 발생해야 한다.") {

                    shouldThrow<ProductException> {
                        productCommandService.delete(productVO)
                    }
                }
            }

            When("category, brand, price 로 찾은 데이터가 있어서 삭제할 때") {
                val product = Product(1L, "", "", BigDecimal.ZERO)
                val minProductVO = ProductInfoVO(1L, "상의", "A", BigDecimal(10))

                every { findProductPort.findProductByCategoryAndBrand(any(), any()) } returns listOf(productVO, productVO)
                every { findProductPort.findMinProduct(any(), any()) } returns minProductVO
                every { findProductPort.findById(any()) } returns product

                Then("삭제 완료 후 true 를 return 한다.") {
                    every { deleteProductPort.delete(any()) } returns mockk()

                    val result = productCommandService.delete(productVO)

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

            When("들어온 정보의 brand 가 없는 경우") {
                every { findBrandPort.findExistBrand(any()) } returns null

                Then("update 할 브랜드가 없는 경우 ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
                }
            }

            When("들어온 정보의 id로 조회한 정보가 없을 때") {
                every { findBrandPort.findExistBrand(any()) } returns "A"
                every { findProductPort.findById(any()) } returns null

                Then("update 할 대상이 없는 경우 ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
                }
            }

            When("brand, category 기준으로 검색한 결과가 1개이고 && 업데이트 하려는 값이 origin 과 브랜드, 카테고리가 다른 경우도 업데이트 불가") {
                val product = Product(1L, "A", "바지", BigDecimal.ZERO)

                every { findBrandPort.findExistBrand(any()) } returns "A"
                every { findProductPort.findById(any()) } returns product
                every {findProductPort.findProductId(any(), any(), null) } returns listOf(1L)


                Then("ProductException 발생") {
                    shouldThrow<ProductException> {
                        productCommandService.update(productVO)
                    }
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