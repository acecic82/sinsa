package com.sinsa.application.outport

import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import java.math.BigDecimal

interface FindProductPort {
    fun findLowestCategoryList() : List<ProductInfoVO>
    fun findAllBrandSumPrice(limit: Long) : List<BrandAndPriceVO>
    fun findLowestBrandProductList(brandList: List<String>) : List<ProductInfoVO>
    fun findLowestListByCategory(category: String, limit: Long) : List<BrandAndPriceVO>
    fun findHighestListByCategory(category: String, limit: Long) : List<BrandAndPriceVO>
    fun findProductId(category:String, brand: String, price: BigDecimal): List<Long?>
    fun findById(id: Long): Product?
}