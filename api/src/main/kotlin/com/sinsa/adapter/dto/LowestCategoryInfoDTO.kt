package com.sinsa.adapter.dto

import com.sinsa.application.vo.ProductInfoVO
import java.math.BigDecimal

data class LowestCategoryInfoDTO(
    val categoryAndBrandInfoVOList: List<ProductInfoVO>,
    val totalPrice: BigDecimal
) {
    companion object {
        fun from(voList: List<ProductInfoVO>): LowestCategoryInfoDTO {
            val totalPrice = voList.sumOf { it.price }

            return LowestCategoryInfoDTO(voList, totalPrice)
        }

    }
}
