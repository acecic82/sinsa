package com.sinsa.application.inport

import com.sinsa.application.vo.LowHighBrandInfoVO

interface FindLowAndHighBrandUseCase {
    fun findLowAndHighBrand(category: String): LowHighBrandInfoVO
}