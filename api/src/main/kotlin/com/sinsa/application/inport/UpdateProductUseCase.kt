package com.sinsa.application.inport

import com.sinsa.application.vo.ProductInfoVO

interface UpdateProductUseCase {
    fun update(productVO: ProductInfoVO): Boolean
}