package com.sinsa.application.inport

import com.sinsa.application.vo.ProductInfoVO

interface SaveProductUseCase {
    fun save(product: ProductInfoVO): Boolean
}