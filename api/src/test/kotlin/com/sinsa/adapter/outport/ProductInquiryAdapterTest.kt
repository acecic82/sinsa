package com.sinsa.adapter.outport

import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import com.sinsa.entity.ProductEntity
import com.sinsa.repositories.MinProductJpaRepository
import com.sinsa.repositories.ProductJpaRepository
import com.sinsa.vo.BrandAndPrice
import com.sinsa.vo.ProductInfo
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

internal class ProductInquiryAdapterTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    init {
        val productJpaRepository = mockk<ProductJpaRepository>()
        val minProductJpaRepository = mockk<MinProductJpaRepository>()
        val productInquiryAdapter = ProductInquiryAdapter(productJpaRepository, minProductJpaRepository)

        Given("ID가 주어진 경우") {
            When("조회한 경우가 null 일 때") {
                every { productJpaRepository.findById(any()).getOrNull() } returns null

                Then("결과를 Null return 해야 한다.") {
                    val result = productInquiryAdapter.findById(1L)
                    assertEquals(result, null)
                }
            }

            When("조회한 데이터가 있을 때") {
                val product = ProductEntity(null, "상의", "A", BigDecimal(10_000))
                every { productJpaRepository.findById(any()).getOrNull() } returns product

                val expectedResult = Product(null,  "상의", "A", BigDecimal(10_000))

                Then("Domain product 와 동일한 결과가 나와야한다.") {
                    val realResult = productInquiryAdapter.findById(1L)
                    assertEquals(expectedResult, realResult)
                }
            }
        }

        Given("category, brand, price 가 주어진 경우") {
            When("조회한 아이디 리스트가 있을 때") {
                val idList = listOf(1L)

                every { productJpaRepository.findProductId(any(), any(), any()) } returns idList

                Then("repository 가 반환한 값을 반환해야 한다.") {
                    val result = productInquiryAdapter.findProductId("category", "brand", BigDecimal(100))
                    assertEquals(result, idList)
                }
            }
        }

        Given("category 이름이 주어진 경우") {
            val minBrandAndPriceList = listOf(BrandAndPrice("A", BigDecimal(100)), BrandAndPrice("B", BigDecimal(200)))
            val maxBrandAndPriceList = listOf(BrandAndPrice("C", BigDecimal(300)), BrandAndPrice("D", BigDecimal(400)))

            When("최소값의 brand list 를 찾을 때") {
                every { productJpaRepository.findHighestInfoByCategory(any(), any()) } returns minBrandAndPriceList

                Then("VO로 변환한 값을 return 해야한다.") {
                    val voList = minBrandAndPriceList.map { BrandAndPriceVO(it.brand, it.price) }

                    val result = productInquiryAdapter.findHighestListByCategory("category", 10L)

                    assertEquals(voList.size, result.size)

                    for(i in 0..voList.size - 1) {
                        assertEquals(voList[i].brand, result[i].brand)
                        assertEquals(voList[i].price, result[i].price)
                    }
                }
            }

            When("최대값 brand list 를 찾을 때") {
                every { productJpaRepository.findLowestInfoByCategory(any(), any()) } returns maxBrandAndPriceList

                Then("VO로 변환한 값을 return 해야한다.") {
                    val voList = maxBrandAndPriceList.map { BrandAndPriceVO(it.brand, it.price) }

                    val result = productInquiryAdapter.findLowestListByCategory("category", 10L)

                    assertEquals(voList.size, result.size)

                    for(i in 0..voList.size - 1) {
                        assertEquals(voList[i].brand, result[i].brand)
                        assertEquals(voList[i].price, result[i].price)
                    }
                }
            }
        }

        Given("brand list 가 주어진 경우") {
            val product = ProductInfo(1L, "상의", "A", BigDecimal(100))

            When("ProductInfo 를 Return 했을 때") {
                every { minProductJpaRepository.findProductListByBrandList(any()) } returns listOf(product)

                Then("ProductInfoVO 로 변환해서 반환해야 한다.") {
                    val voList = listOf(ProductInfoVO(product.id, product.category, product.brand, product.price))

                    val result = productInquiryAdapter.findBrandMinProductList(listOf("A"))

                    assertEquals(result.size, voList.size)

                    for(i in 0..result.size-1) {
                        assertEquals(result[i].productId, voList[i].productId)
                        assertEquals(result[i].category, voList[i].category)
                        assertEquals(result[i].brand, voList[i].brand)
                        assertEquals(result[i].price, voList[i].price)
                    }
                }
            }
        }

        Given("브랜드별 항목들의 가격 합계를 구하는 경우") {
            When("brand 와 가격의 정보를 반환 했을 때") {
                val brandAndPriceList = listOf(BrandAndPrice("A", BigDecimal(100)), BrandAndPrice("B", BigDecimal(200)))

                every { minProductJpaRepository.findAllBrandSumPrice(any()) } returns brandAndPriceList

                Then("VO로 변환한 값을 반환해야 한다.") {
                    val voList = brandAndPriceList.map {
                        BrandAndPriceVO(it.brand, it.price)
                    }

                    val result = productInquiryAdapter.findAllBrandSumPriceFromMinProduct(1L)

                    assertEquals(voList.size, result.size)

                    for(i in 0..voList.size - 1) {
                        assertEquals(voList[i].brand, result[i].brand)
                        assertEquals(voList[i].price, result[i].price)
                    }
                }
            }
        }

        Given("카테고리 별로 가장 낮은 가격의 브랜드를 찾아야 하는 경우") {
            When("ProductInfo 를 return 했을 때") {
                val productList = listOf( ProductInfo(1L, "상의", "A", BigDecimal(100)))

                every { productJpaRepository.findLowestPriceAllCategoryInfo() } returns productList

                Then("VO로 변환한 list 를 반환해야 한다.") {
                    val voList = productList.map { ProductInfoVO(it.id, it.category, it.brand, it.price) }

                    val result = productInquiryAdapter.findLowestCategoryList()

                    assertEquals(result.size, voList.size)

                    for(i in 0..result.size-1) {
                        assertEquals(result[i].productId, voList[i].productId)
                        assertEquals(result[i].category, voList[i].category)
                        assertEquals(result[i].brand, voList[i].brand)
                        assertEquals(result[i].price, voList[i].price)
                    }
                }
            }
        }

        Given("다 찾는 경우") {
            When("전체 조회를 할 때") {
                val product = ProductEntity(1L, "상의", "A", BigDecimal(1_000))

                every { productJpaRepository.findAll() } returns listOf(product)

                Then("Product 로 반환한 값을 보내야 한다.") {
                    val expectedOutPut = listOf(Product(1L, "상의", "A", BigDecimal(1_000)))

                    val result = productInquiryAdapter.findAll()

                    assertEquals(result.size, expectedOutPut.size)

                    for(i in  0..result.size - 1) {
                        assertEquals(result[i].productId, expectedOutPut[i].productId)
                        assertEquals(result[i].category, expectedOutPut[i].category)
                        assertEquals(result[i].brand, expectedOutPut[i].brand)
                        assertEquals(result[i].price, expectedOutPut[i].price)
                    }
                }
            }
        }
    }
}