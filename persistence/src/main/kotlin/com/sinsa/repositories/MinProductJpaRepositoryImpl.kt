package com.sinsa.repositories

import com.querydsl.core.types.Projections
import com.sinsa.entity.MinProductEntity
import com.sinsa.vo.BrandAndPrice
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import com.sinsa.entity.QMinProductEntity.Companion.minProductEntity
import com.sinsa.vo.ProductInfo

@Repository
class MinProductJpaRepositoryImpl: MinProductJpaRepositoryCustom, QuerydslRepositorySupport(MinProductEntity::class.java) {
    override fun findAllBrandSumPrice(limit: Long): List<BrandAndPrice> {
        return from(minProductEntity)
            .groupBy(minProductEntity.brand)
            .select(
                Projections.constructor(
                    BrandAndPrice::class.java,
                    minProductEntity.brand,
                    minProductEntity.price.sum()
                )
            )
            .fetch()
    }

    override fun findProductListByBrandList(brandList: List<String>): List<ProductInfo> {
        return from(minProductEntity)
            .where(minProductEntity.brand.`in`(brandList))
            .select(
                Projections.constructor(
                    ProductInfo::class.java,
                    minProductEntity.id,
                    minProductEntity.category,
                    minProductEntity.brand,
                    minProductEntity.price
                )
            )
            .fetch()
    }
}