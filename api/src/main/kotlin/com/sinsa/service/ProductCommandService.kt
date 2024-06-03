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
        // 들어온 id 가 있다면 해당 ID로 리스트를 만들고 없다면 category,brand,price 가 일치하는
        // 항목을 지워야할 대상으로 삼는다.
        val productIdList = vo.productId?.let {
            listOf(findProductPort.findById(it)?.productId)
        } ?: findProductPort.findProductId(vo.category, vo.brand, vo.price)

        // id가 실제 테이블에 존재하는 않는 경우엔 Exception 을 발생시켜 삭제에 실패했음을 알린다.
        productIdList.forEach {
            it?.let { deleteProductPort.delete(it) }
                ?: throw ProductException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.message)
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