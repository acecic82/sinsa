package com.sinsa.application.inport

import com.sinsa.application.vo.ProductInfoVO

interface FindAllProductUseCase {
    fun findAllProduct(): List<ProductInfoVO>
}