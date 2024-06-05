package com.sinsa.adapter.outport

import com.sinsa.application.outport.FindBrandPort
import com.sinsa.repositories.BrandJpaRepository
import org.springframework.stereotype.Component

@Component
class BrandInquiryAdapter(
    private val brandJpaRepository: BrandJpaRepository
) : FindBrandPort {
    override fun findExistBrand(brand: String): String? {
        return brandJpaRepository.findExistBrand(brand)
    }
}