package com.sinsa.repositories

import com.sinsa.entity.MinProductEntity
import com.sinsa.vo.BrandAndPrice
import com.sinsa.vo.ProductInfo
import org.springframework.data.jpa.repository.JpaRepository

interface MinProductJpaRepository: JpaRepository<MinProductEntity, Long>, MinProductJpaRepositoryCustom {
}

interface MinProductJpaRepositoryCustom {
    fun findAllBrandSumPrice(limit: Long): List<BrandAndPrice>

    fun findProductListByBrandList(brandList: List<String>): List<ProductInfo>

    fun findProductByCategoryAndBrand(category: String, brand: String): ProductInfo?
}