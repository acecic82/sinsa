package com.sinsa.application.vo

data class LowHighBrandInfoVO(
    val category: String,
    val lowestBrandList: List<BrandAndPriceVO>,
    val highestBrandList: List<BrandAndPriceVO>
)
