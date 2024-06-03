package com.sinsa.adapter.inport

import com.sinsa.adapter.dto.ProductInfoDTO
import com.sinsa.application.inport.DeleteProductUseCase
import com.sinsa.application.inport.SaveProductUseCase
import com.sinsa.application.inport.UpdateProductUseCase
import com.sinsa.common.response.SuccessResponseDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductCommandController(
    private val deleteProductUseCase: DeleteProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val saveProductUseCase: SaveProductUseCase
) {
    @PostMapping("/save")
    fun save(@RequestBody productInfoDTO: ProductInfoDTO): SuccessResponseDTO<Boolean> {
        return SuccessResponseDTO.success(saveProductUseCase.save(productInfoDTO.toVO()))
    }

    @PutMapping("/update")
    fun update(@RequestBody productInfoDTO: ProductInfoDTO): SuccessResponseDTO<Boolean> {
        return SuccessResponseDTO.success(updateProductUseCase.update(productInfoDTO.toVO()))
    }

    @DeleteMapping("/delete")
    fun delete(@RequestBody productInfoDTO: ProductInfoDTO): SuccessResponseDTO<Boolean> {
        return SuccessResponseDTO.success(deleteProductUseCase.delete(productInfoDTO.toVO()))
    }
}