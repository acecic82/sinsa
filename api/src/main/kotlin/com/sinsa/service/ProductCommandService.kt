package com.sinsa.service

import com.sinsa.application.inport.DeleteProductUseCase
import com.sinsa.application.inport.SaveProductUseCase
import com.sinsa.application.inport.UpdateProductUseCase
import com.sinsa.application.outport.DeleteProductPort
import com.sinsa.application.outport.FindBrandPort
import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.outport.SaveProductPort
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product
import com.sinsa.response.ProductException
import com.sinsa.response.enum.ExceptionCode.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductCommandService(
    private val findProductPort: FindProductPort,
    private val deleteProductPort: DeleteProductPort,
    private val saveProductPort: SaveProductPort,
    private val findBrandPort: FindBrandPort,

) : DeleteProductUseCase, SaveProductUseCase, UpdateProductUseCase {

    @Transactional
    override fun delete(vo: ProductInfoVO): Boolean {
        //들어온 카테고리, Brand 기준으로 productId를 뽑아낸다.
        //만약 productId 들이 1개라면 삭제할 수 없다. 왜냐하면 category & brand 를 기준으로
        //상품이 최소 1개 존재해야 하기 떄문입니다.
        val productIdList = findProductPort.findProductId(vo.category, vo.brand, null)

        require(productIdList.size > 1) {
            throw ProductException(PRODUCT_CATEGORY_BRAND_NOT_ENOUGH, PRODUCT_CATEGORY_BRAND_NOT_ENOUGH.message)
        }

        //삭제할 대상 확정하기
        //id가 없다면 category, brand, price 를 기준으로 삭제를 시도합니다.
        //같은 브랜드, 카테고리, 가격의 상품이 존재하면 같이 지워질 수 있지만, 오류가 없다면 id 가 null 로
        //들어오는 경우는 없기 때문에 잘 발생하기 않고 3가지 조건이 모두 겹칠 가능성도 크지 않기 떄문에
        //삭제를 이런식으로 처리해봤습니다.

        val deleteCandidate = vo.productId?.let {
            listOf(findProductPort.findById(it)?.productId)
        } ?: findProductPort.findProductId(vo.category, vo.brand, vo.price)

        // id가 실제 테이블에 존재하는 않는 경우엔 Exception 을 발생시켜 삭제에 실패했음을 알린다.
        deleteCandidate.forEach {
            it?.let {
                deleteProductPort.delete(it)
            } ?: throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        return true
    }

    @Transactional
    override fun save(product: ProductInfoVO): Boolean {
        //브랜드가 존재하는지 확인한다
        //존재하지 않는다면 Exception
        require(findBrandPort.findExistBrand(product.brand) != null) {
            throw ProductException(BRAND_NOT_EXIST, BRAND_NOT_EXIST.message)
        }

        //minProduct 의 정보를 갱신할 필요가 있는지 확인한다.
        val minProduct = findProductPort.findMinProduct(product.category, product.brand)

        require(minProduct != null) {
            throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        //현재 minProduct 의 가격보다 더 낮은 product 가 들어온것이라면 minProduct 정보도 같이 갱신한다.

        checkAndSaveMinProduct(minProduct, product.price)


        // 저장한다
        val savedProduct = saveProductPort.save(product.toProduct())

        //저장 데이터와 저장된 데이터가 같은지 비교하여 다르다면 실패로 간주한다.
        if(!savedProduct.isSame(product.toProduct()))
            throw ProductException(PRODUCT_SAVE_FAIL, PRODUCT_SAVE_FAIL.message)

        return true
    }

    @Transactional
    override fun update(productVO: ProductInfoVO): Boolean {
        //update 시엔 productId를 필수적으로 받도록 한다 그래야 어떤 제품을 업데이트 하기 위함인지 알 수 있다.
        require(productVO.productId != null) {
            throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        //브랜드가 존재하는지 확인한다
        //존재하지 않는다면 Exception
        require(findBrandPort.findExistBrand(productVO.brand) != null) {
            throw ProductException(BRAND_NOT_EXIST, BRAND_NOT_EXIST.message)
        }

        //update 하려는 대상이 brand & category 조건으로 볼 때 단 1개 밖에 없고, 업데이트 하려는것이 값이
        //category or brand 라면 그 경우는 업데이트 안되는 조건으로 봅니다.
        //왜냐하면 만약 update 하려는 대상이 brand & category 조건으로 업데이트 대상이 1개 밖에 없는데
        //category 나 update 를 바꿔버리게 되면 브랜드 카테고리당 상품이 1개 존재한다는 전제조건이 무너지기 떄문에
        //업데이트 불가한 상태로 보게됩니다.
        val originProduct = findProductPort.findById(productVO.productId)

        //productId로 검색한 결과가 없을 경우 업데이트 불가
        require(originProduct != null) {
            throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        //brand, category 기준으로 검색한 결과가 1개이고 && 업데이트하려는 값이 브랜드, 카테고리인 경우도 업데이트 불가
        val productIdList = findProductPort.findProductId(originProduct.category, originProduct.brand, null)

        if (productIdList.size == 1 && (originProduct.brand != productVO.brand || originProduct.category != productVO.category)) {
            throw ProductException(PRODUCT_CATEGORY_BRAND_NOT_ENOUGH_UPDATE, PRODUCT_CATEGORY_BRAND_NOT_ENOUGH_UPDATE.message)
        }

        val targetProduct = productVO.toProduct()

        //minProduct 도 업데이트 할 필요가 있는지 살펴본다.

        executeMinProduct(originProduct, targetProduct)

        originProduct.update(targetProduct)
        val updatedProduct = saveProductPort.save(originProduct)

        //update 된 데이터와 타겟 데이터가 다르다면 업데이트가 안된것으로 간주한다. 업데이트가 하나라도 실패하면
        //Transactional 을 이용하여 전체 실패하도록 한다.
        if (!updatedProduct.isSame(targetProduct)) {
            throw ProductException(PRODUCT_UPDATE_FAIL, PRODUCT_UPDATE_FAIL.message)
        }

        return true
    }

    private fun executeMinProduct(
        originProduct: Product,
        targetProduct: Product
    ) {
        //조건 1. origin, target 의 category, brand 값이 일치하는 경우 minProduct 의 값과 target의 값중 작은 값으로 업데이트 하면 된다.
        if (originProduct.category == targetProduct.category && originProduct.brand == targetProduct.brand) {
            val minProduct = findProductPort.findMinProduct(originProduct.category, originProduct.brand)

            require(minProduct != null) {
                throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
            }

            checkAndSaveMinProduct(minProduct, targetProduct.price)
            return
        }

        // origin 과 target 이 달라지는 경우는 생각할게 조금 많아진다.
        // target 은 그냥 저장하면 되지만, origin 의 경우는 만약 바꿀 대상이 origin 의 category & brand 조건으로 봤을 때
        // minPrice 였다면 2번쨰로 큰 minPrice 로 바꿔줘야 한다.
        val originList = findProductPort.findProductByCategoryAndBrand(originProduct.category, originProduct.brand)

        // 현재 바뀌게 되는 origin 의 productId를 제외한 origin 의 brand & category list 를 뽑아냅니다.
        val exceptedProductList = originList.filter { it.productId!! != originProduct.productId}

        require(exceptedProductList.isNotEmpty()) {
            throw ProductException(PRODUCT_CATEGORY_BRAND_NOT_ENOUGH_UPDATE, PRODUCT_CATEGORY_BRAND_NOT_ENOUGH_UPDATE.message)
        }

        val originMinPrice = exceptedProductList.minBy { it.price }.price

        val originMinProduct = findProductPort.findMinProduct(originProduct.category, originProduct.brand)

        require(originMinProduct != null) {
            throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        // Origin 은 바꾸려는 대상을 제외한 minPrice 로 강제 업데이트 한다.
        // 현재 바꾸려는 대상이 origin 의 brand & category 에서 min price 였다면 2번쨰로 작은 값이 originMinPrice 가 됐을것이고
        // 현재 바꾸려는 대상이 origin 의 brand & category 에서 min Price 가 아닌 경우라면 기존 minPrice 로 업데이트 될 것이다.
        checkAndSaveMinProduct(originMinProduct, originMinPrice, true)

        val minTargetProduct = findProductPort.findMinProduct(targetProduct.category, targetProduct.brand)

        require(minTargetProduct != null) {
            throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        //target 은 비교적 간단하고 바꾸려는 금액과 현재 Target category & brand 의 가격중 작은 값을 넣어주면 된다.
        checkAndSaveMinProduct(minTargetProduct, targetProduct.price)
    }

    private fun checkAndSaveMinProduct(minProduct: ProductInfoVO, targetPrice : BigDecimal, saveForce: Boolean = false) {
        if (minProduct.price > targetPrice || saveForce) {
            minProduct.price = targetPrice

            val savedProduct = saveProductPort.saveMinProduct(minProduct.toProduct())

            //저장 데이터와 저장된 데이터가 같은지 비교하여 다르다면 실패로 간주한다.
            if (!savedProduct.isSame(minProduct.toProduct()))
                throw ProductException(PRODUCT_SAVE_FAIL, PRODUCT_SAVE_FAIL.message)
        }
    }
}