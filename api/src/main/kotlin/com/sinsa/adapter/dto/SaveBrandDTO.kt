package com.sinsa.adapter.dto

import com.sinsa.application.vo.SaveBrandVO

data class SaveBrandDTO(
    val brand: String,
    val productList: List<ProductInfoDTO>
) {
    fun toVO(): SaveBrandVO {
        val voList = productList.map { it.toVO() }

        return SaveBrandVO(brand, voList)
    }
}
