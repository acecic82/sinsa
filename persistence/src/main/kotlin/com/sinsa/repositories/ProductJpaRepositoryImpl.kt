package com.sinsa.repositories

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.sql.JPASQLQuery
import com.querydsl.sql.SQLTemplates
import com.sinsa.entity.ProductEntity
import com.sinsa.entity.QProductEntity.Companion.productEntity
import com.sinsa.vo.BrandAndPrice
import com.sinsa.vo.CategoryAndPrice
import com.sinsa.vo.ProductInfo
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class ProductJpaRepositoryImpl(
    val sqlTemplates: SQLTemplates
) : QuerydslRepositorySupport(ProductEntity::class.java), ProductJpaRepositoryCustom {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    //쿼리가 다소 길어졌지만 1번의 쿼리로 해결하기 위해 이렇게 했습니다.
    //이렇게 안하면 groupBy 쿼리에 모든 카테고리별로 쿼리가 하나씩 더 나가야해서 N+1과 비슷한 상태가 될 수 있어
    //쿼리가 길어져도 1번의 쿼리로 해결하려고 하였습니다.
    override fun findLowestPriceAllCategoryInfo(): List<ProductInfo> {
        val jpaSqlQuery: JPASQLQuery<*> = JPASQLQuery<Any?>(entityManager, sqlTemplates)
        val subQueryProduct = Expressions.stringPath("sub_query_product")

        return jpaSqlQuery
                .from(
                    productEntity
                ).innerJoin(
                    JPAExpressions.select(
                        Projections.constructor(
                            CategoryAndPrice::class.java,
                            productEntity.category.`as`(CATEGORY),
                            productEntity.price.min().`as`(PRICE)
                        )
                    )
                        .from(productEntity)
                        .groupBy(productEntity.category),
                    subQueryProduct
                ).on(
                    productEntity.category.eq(Expressions.stringPath(subQueryProduct, CATEGORY)).and(
                        productEntity.price.eq(Expressions.numberPath(BigDecimal::class.java, subQueryProduct, PRICE))
                    )
                )
                .select(Projections.constructor(ProductInfo::class.java,
                    productEntity.id,
                    productEntity.category,
                    productEntity.brand,
                    productEntity.price))
                .fetch()
    }

    override fun findAllBrandSumPrice(limit: Long): List<BrandAndPrice> {
        return from(productEntity)
            .groupBy(productEntity.brand)
            .orderBy(productEntity.price.sum().asc())
            .limit(limit)
            .select(
                Projections.constructor(
                    BrandAndPrice::class.java,
                    productEntity.brand,
                    productEntity.price.sum()
                )
            )
            .fetch()
    }

    override fun findLowestTotalPrice(brandList: List<String>): List<ProductInfo> {
        return from(productEntity)
            .where(productEntity.brand.`in`(brandList))
            .select(
                Projections.constructor(
                    ProductInfo::class.java,
                    productEntity.id,
                    productEntity.category,
                    productEntity.brand,
                    productEntity.price
                )
            )
            .fetch()
    }

    override fun findLowestInfoByCategory(category: String, limit: Long): List<BrandAndPrice> {
        return from(productEntity)
            .where(productEntity.category.eq(category))
            .orderBy(productEntity.price.asc())
            .select(
                Projections.constructor(
                    BrandAndPrice::class.java,
                    productEntity.brand,
                    productEntity.price
                )
            )
            .fetch()
    }

    override fun findHighestInfoByCategory(category: String, limit: Long): List<BrandAndPrice> {
        return from(productEntity)
            .where(productEntity.category.eq(category))
            .orderBy(productEntity.price.desc())
            .select(
                Projections.constructor(
                    BrandAndPrice::class.java,
                    productEntity.brand,
                    productEntity.price
                )
            )
            .fetch()
    }

    override fun findProductId(category: String, brand: String, price: BigDecimal): List<Long?> {
        return from(productEntity)
            .where(productEntity.category.eq(category)
                .and(productEntity.brand.eq(brand))
                .and(productEntity.price.eq(price)))
            .select(productEntity.id)
            .fetch()
    }

    companion object {
        const val CATEGORY = "category"
        const val PRICE = "price"
    }
}