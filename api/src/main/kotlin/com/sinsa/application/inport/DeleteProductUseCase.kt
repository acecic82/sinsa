package com.sinsa.application.inport

import com.sinsa.application.vo.ProductInfoVO

interface DeleteProductUseCase {
    fun delete(vo: ProductInfoVO) : Boolean
}