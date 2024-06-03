package com.sinsa.application.outport

import com.sinsa.entity.Product

interface SaveProductPort {
    fun save(product: Product): Product
}