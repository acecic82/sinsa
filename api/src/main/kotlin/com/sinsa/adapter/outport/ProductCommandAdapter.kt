package com.sinsa.adapter.outport

import com.sinsa.application.outport.DeleteProductPort
import com.sinsa.application.outport.SaveProductPort
import com.sinsa.entity.Product
import com.sinsa.entity.ProductEntity
import com.sinsa.repositories.ProductJpaRepository
import org.springframework.stereotype.Component

@Component
class ProductCommandAdapter(
    private val productJpaRepository: ProductJpaRepository
) : DeleteProductPort, SaveProductPort {
    override fun delete(id: Long) {
        return productJpaRepository.deleteById(id)
    }

    override fun save(product: Product): Product {
        return productJpaRepository.save(ProductEntity.from(product)).toProduct()
    }
}