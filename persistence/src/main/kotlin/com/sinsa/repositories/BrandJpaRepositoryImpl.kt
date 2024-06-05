package com.sinsa.repositories

import com.querydsl.core.types.Projections
import com.sinsa.entity.BrandEntity
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import com.sinsa.entity.QBrandEntity.Companion.brandEntity

@Repository
class BrandJpaRepositoryImpl: QuerydslRepositorySupport(BrandEntity::class.java), BrandJpaRepositoryCustom {
    override fun findExistBrand(brand: String): String? {
        return from(brandEntity)
            .where(brandEntity.brand.eq(brand))
            .select(
                Projections.constructor(
                    String::class.java,
                    brandEntity.brand
                )
            )
            .fetchOne()
        //Brand는 unique key 이기 때문에 brand를 equal로 찾을 경우 1개만 나오게 됩니다.
    }
}