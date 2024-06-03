package com.sinsa.adapter.dto

import com.sinsa.application.vo.CategoryAndPriceVO
import com.sinsa.application.vo.ProductInfoVO
import java.math.BigDecimal

data class LowestBrandInfoDTO(
    val brand: String,
    val categoryAndPriceList: List<CategoryAndPriceVO>,
    val totalPrice: BigDecimal
) {
    companion object {
        fun fromBrand(brand: String, voList: List<ProductInfoVO>): LowestBrandInfoDTO {
            val categoryAndPriceList = voList.map {
                CategoryAndPriceVO(it.category, it.price)
            }

            val totalPrice = voList.sumOf { it.price }

            return LowestBrandInfoDTO(brand, categoryAndPriceList, totalPrice)
        }
    }
}

