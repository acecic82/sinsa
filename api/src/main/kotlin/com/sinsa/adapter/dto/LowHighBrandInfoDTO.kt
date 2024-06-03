package com.sinsa.adapter.dto

import com.sinsa.application.vo.BrandAndPriceVO
import com.sinsa.application.vo.LowHighBrandInfoVO

data class LowHighBrandInfoDTO(
    val category: String,
    val lowestBrandList: List<BrandAndPriceVO>,
    val highestBrandList: List<BrandAndPriceVO>
) {
    companion object {
        fun from(vo: LowHighBrandInfoVO) = LowHighBrandInfoDTO(
            vo.category,
            vo.lowestBrandList,
            vo.highestBrandList
        )
    }
}
