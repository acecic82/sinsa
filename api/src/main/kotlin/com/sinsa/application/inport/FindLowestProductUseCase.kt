package com.sinsa.application.inport

import com.sinsa.application.vo.ProductInfoVO

interface FindLowestProductUseCase {
    fun findLowestPriceAllCategory(): List<ProductInfoVO>

    fun findLowestPriceAllBrand(): List<ProductInfoVO>
}