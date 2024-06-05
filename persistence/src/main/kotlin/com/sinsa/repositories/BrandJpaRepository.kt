package com.sinsa.repositories

import com.sinsa.entity.BrandEntity
import org.springframework.data.jpa.repository.JpaRepository


interface BrandJpaRepository: JpaRepository<BrandEntity, Long>, BrandJpaRepositoryCustom {
}

interface BrandJpaRepositoryCustom {
    fun findExistBrand(brand: String): String?
}