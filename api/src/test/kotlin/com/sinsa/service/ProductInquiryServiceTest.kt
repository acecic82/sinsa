package com.sinsa.service

import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.LowHighBrandInfoVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal

internal class ProductInquiryServiceTest: BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    init {
        val findProductPort = mockk<FindProductPort>()

        val productInquiryService = ProductInquiryService(findProductPort)

        Given("findLowestPriceAllCategory") {
            When("모드 카테고리에서 최저가의 브랜드 정보를 찾아낼 때") {
                every { findProductPort.findLowestCategoryList() } returns listOf()

                Then("list 정보 return") {
                    val result = productInquiryService.findLowestPriceAllCategory()

                    assertEquals(result, listOf<ProductInfoVO>())
                }
            }
        }

        Given("findLowestPriceAllBrand") {
            When("brand list 를 잘 반환했을 때") {
                val brandPrice = BrandAndPriceVO("A", BigDecimal(1_000))
                val brandPrice2 = BrandAndPriceVO("B", BigDecimal(1_200))
                val brandPrice3 = BrandAndPriceVO("C", BigDecimal(1_000))
                val productA = ProductInfoVO(null, "", "A", BigDecimal(1_000))
                val productC = ProductInfoVO(null, "", "C", BigDecimal(1_000))

                every { findProductPort.findAllBrandSumPriceFromMinProduct(any()) } returns listOf(
                    brandPrice,
                    brandPrice2,
                    brandPrice3
                )

                every { findProductPort.findBrandMinProductList(any()) } returns listOf(productA, productC)

                Then("최저가의 product 정보를 return 한다") {
                    val expectedList = listOf(productA, productC)

                    val result = productInquiryService.findLowestPriceAllBrand()

                    assertEquals(result.size, expectedList.size)

                    for(i in 0..result.size - 1) {
                        assertEquals(result[i].productId, expectedList[i].productId)
                        assertEquals(result[i].category, expectedList[i].category)
                        assertEquals(result[i].brand, expectedList[i].brand)
                        assertEquals(result[i].price, expectedList[i].price)
                    }
                }
            }
        }

        Given("category 정보가 있는 경우") {
            val brandPrice = BrandAndPriceVO("A", BigDecimal(1_000))
            val brandPrice2 = BrandAndPriceVO("B", BigDecimal(1_200))
            val brandPrice3 = BrandAndPriceVO("C", BigDecimal(1_400))

            When("최대, 최소가 있는 경우") {
                val candidateList = listOf(brandPrice, brandPrice2, brandPrice3)

                every { findProductPort.findLowestListByCategory(any(), any()) } returns candidateList
                every { findProductPort.findHighestListByCategory(any(), any()) } returns candidateList

                Then("카테고리, 최소, 최대 값을 return 한다") {
                    val expectedResult = LowHighBrandInfoVO("A", listOf(brandPrice), listOf(brandPrice3))

                    val result = productInquiryService.findLowAndHighBrand("A")

                    assertEquals(result.category, expectedResult.category)

                    assertEquals(result.lowestBrandList.size, expectedResult.lowestBrandList.size)
                    assertEquals(result.highestBrandList.size, expectedResult.highestBrandList.size)

                    for (i in 0..result.lowestBrandList.size - 1) {
                        assertEquals(result.lowestBrandList[i].brand, expectedResult.lowestBrandList[i].brand)
                        assertEquals(result.lowestBrandList[i].price, expectedResult.lowestBrandList[i].price)
                    }

                    for (i in 0..result.highestBrandList.size - 1) {
                        assertEquals(result.highestBrandList[i].brand, expectedResult.highestBrandList[i].brand)
                        assertEquals(result.highestBrandList[i].price, expectedResult.highestBrandList[i].price)
                    }
                }
            }
        }

        Given("findAllProduct") {
            When("모든 product 를 조회할 때") {
                val product = Product(1L, "상의", "A", BigDecimal(1_000))

                every { findProductPort.findAll() } returns listOf(product)

                Then("VO로 바뀐 형태를 return 해야 한다.") {
                    val expectedResult =
                        listOf(ProductInfoVO(product.productId, product.category, product.brand, product.price))

                    val result = productInquiryService.findAllProduct()

                    assertEquals(result.size, expectedResult.size)

                    for(i in  0..result.size - 1) {
                        assertEquals(result[i].productId, expectedResult[i].productId)
                        assertEquals(result[i].category, expectedResult[i].category)
                        assertEquals(result[i].brand, expectedResult[i].brand)
                        assertEquals(result[i].price, expectedResult[i].price)
                    }

                }
            }
        }
    }
}