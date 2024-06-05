package com.sinsa.service

import com.sinsa.application.inport.DeleteProductUseCase
import com.sinsa.application.inport.SaveProductUseCase
import com.sinsa.application.inport.UpdateProductUseCase
import com.sinsa.application.outport.DeleteProductPort
import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.outport.SaveProductPort
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.response.ProductException
import com.sinsa.response.enum.ExceptionCode.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductCommandService(
    private val findProductPort: FindProductPort,
    private val deleteProductPort: DeleteProductPort,
    private val saveProductPort: SaveProductPort
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
        productIdList.forEach {
            it?.let {
                deleteProductPort.delete(it)
            } ?: throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
        }

        return true
    }

    @Transactional
    override fun save(product: ProductInfoVO): Boolean {
        val savedProduct = saveProductPort.save(product.toProduct())

        //저장 데이터와 저장된 데이터가 같은지 비교하여 다르다면 실패로 간주한다.
        if(!savedProduct.isSame(product.toProduct()))
            throw ProductException(PRODUCT_SAVE_FAIL, PRODUCT_SAVE_FAIL.message)

        return true
    }

    @Transactional
    override fun update(productVO: ProductInfoVO): Boolean {
        //update 시엔 productId를 필수적으로 받도록 한다 그래야 어떤 제품을 업데이트 하기 위함인지 알 수 있다.
        val productIdList = productVO.productId?.let {
            listOf(it)
        } ?: throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)


        val product = productIdList.map {
            it.let {
                findProductPort.findById(it)
            }
        }

        val targetProduct = productVO.toProduct()

        product.forEach {
            it?.let {
                it.update(targetProduct)
                val updatedProduct = saveProductPort.save(it)

                //update 된 데이터와 타겟 데이터가 다르다면 업데이트가 안된것으로 간주한다. 업데이트가 하나라도 실패하면
                //Transactional 을 이용하여 전체 실패하도록 한다.
                if (!updatedProduct.isSame(targetProduct)) {
                    throw ProductException(PRODUCT_UPDATE_FAIL, PRODUCT_UPDATE_FAIL.message)
                }
            // 업데이트를 대상으로 하는 id가 실제로 존재하지 않는다면 Update 실패 처리한다.
            } ?: throw ProductException(PRODUCT_UPDATE_FAIL, PRODUCT_UPDATE_FAIL.message)
        }

        return true
    }
}