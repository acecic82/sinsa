package com.sinsa.application.outport

import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import java.math.BigDecimal

interface FindProductPort {
    fun findLowestCategoryList() : List<ProductInfoVO>
    fun findAllBrandSumPriceFromMinProduct(limit: Long) : List<BrandAndPriceVO>
    fun findBrandMinProductList(brandList: List<String>) : List<ProductInfoVO>
    fun findLowestListByCategory(category: String, limit: Long) : List<BrandAndPriceVO>
    fun findHighestListByCategory(category: String, limit: Long) : List<BrandAndPriceVO>
    fun findProductId(category:String, brand: String, price: BigDecimal?): List<Long?>
    fun findById(id: Long): Product?

    fun findAll(): List<Product>

    fun findMinProduct(category: String, brand: String): ProductInfoVO?

    fun findProductByCategoryAndBrand(category: String, brand: String): List<ProductInfoVO>
}