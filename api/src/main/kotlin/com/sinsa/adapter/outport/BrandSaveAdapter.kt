package com.sinsa.adapter.outport

import com.sinsa.application.outport.SaveBrandPort
import com.sinsa.entity.BrandEntity
import com.sinsa.repositories.BrandJpaRepository
import org.springframework.stereotype.Component

@Component
class BrandSaveAdapter(
    private val brandJpaRepository: BrandJpaRepository
): SaveBrandPort {
    override fun save(id: Long?, brand: String): String {
        return brandJpaRepository.save(BrandEntity.makeBrandEntity(id, brand)).brand
    }
}