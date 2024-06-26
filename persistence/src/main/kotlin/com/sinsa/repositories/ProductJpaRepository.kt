package com.sinsa.repositories

import com.sinsa.entity.ProductEntity
import com.sinsa.vo.BrandAndPrice
import com.sinsa.vo.ProductInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal

interface ProductJpaRepository: JpaRepository<ProductEntity, Long>, ProductJpaRepositoryCustom {
}

interface ProductJpaRepositoryCustom {
    fun findLowestPriceAllCategoryInfo(): List<ProductInfo>

    fun findLowestInfoByCategory(category: String, limit: Long): List<BrandAndPrice>

    fun findHighestInfoByCategory(category: String, limit: Long): List<BrandAndPrice>

    fun findProductId(category: String, brand: String, price: BigDecimal?): List<Long?>

    fun findProductByCategoryAndBrand(category: String, brand: String): List<ProductInfo>
}