package com.sinsa.adapter.outport

import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import com.sinsa.repositories.MinProductJpaRepository
import com.sinsa.repositories.ProductJpaRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

@Component
class ProductInquiryAdapter (
    private val productJpaRepository: ProductJpaRepository,
    private val minProductJpaRepository: MinProductJpaRepository
): FindProductPort {
    override fun findLowestCategoryList(): List<ProductInfoVO> {
        return productJpaRepository.findLowestPriceAllCategoryInfo().map {
            ProductInfoVO(it.id, it.category, it.brand, it.price)
        }
    }

    override fun findAllBrandSumPrice(limit: Long): List<BrandAndPriceVO> {
        return minProductJpaRepository.findAllBrandSumPrice(limit).map {
            BrandAndPriceVO(it.brand, it.price)
        }
    }

    override fun findBrandProductList(brandList: List<String>): List<ProductInfoVO> {
        return minProductJpaRepository.findProductListByBrandList(brandList).map {
            ProductInfoVO(it.id, it.category, it.brand, it.price)
        }
    }

    override fun findLowestListByCategory(category: String, limit: Long): List<BrandAndPriceVO> {
        return productJpaRepository.findLowestInfoByCategory(category, limit).map {
            BrandAndPriceVO(it.brand, it.price)
        }
    }

    override fun findHighestListByCategory(category: String, limit: Long): List<BrandAndPriceVO> {
        return productJpaRepository.findHighestInfoByCategory(category, limit).map {
            BrandAndPriceVO(it.brand, it.price)
        }
    }

    override fun findProductId(category: String, brand: String, price: BigDecimal?): List<Long?> {
        return productJpaRepository.findProductId(category, brand, price)
    }

    override fun findById(id: Long): Product? {
        return productJpaRepository.findById(id).getOrNull()?.toProduct()
    }

    override fun findAll(): List<Product> {
        return productJpaRepository.findAll().map {
            it.toProduct()
        }
    }

    override fun findMinProduct(category: String, brand: String): ProductInfoVO? {
        return minProductJpaRepository.findProductByCategoryAndBrand(category, brand)?.let {
            ProductInfoVO(it.id, it.category, it.brand, it.price)
        }
    }

    override fun findProductByCategoryAndBrand(category: String, brand: String): List<ProductInfoVO> {
        return productJpaRepository.findProductByCategoryAndBrand(category, brand).map {
            ProductInfoVO(it.id, it.category, it.brand, it.price)
        }
    }
}