package com.sinsa.adapter.inport

import com.sinsa.adapter.dto.ProductInfoDTO
import com.sinsa.adapter.dto.SaveBrandDTO
import com.sinsa.application.inport.DeleteProductUseCase
import com.sinsa.application.inport.SaveBrandUseCase
import com.sinsa.application.inport.SaveProductUseCase
import com.sinsa.application.inport.UpdateProductUseCase
import com.sinsa.common.response.ResponseDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductCommandController(
    private val deleteProductUseCase: DeleteProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val saveProductUseCase: SaveProductUseCase,
    private val saveBrandUseCase: SaveBrandUseCase
) {
    @PostMapping("/save")
    fun save(@RequestBody productInfoDTO: ProductInfoDTO): ResponseDTO<Boolean> {
        return ResponseDTO.success(saveProductUseCase.save(productInfoDTO.toVO()))
    }

    @PutMapping("/update")
    fun update(@RequestBody productInfoDTO: ProductInfoDTO): ResponseDTO<Boolean> {
        return ResponseDTO.success(updateProductUseCase.update(productInfoDTO.toVO()))
    }

    @DeleteMapping("/delete")
    fun delete(@RequestBody productInfoDTO: ProductInfoDTO): ResponseDTO<Boolean> {
        return ResponseDTO.success(deleteProductUseCase.delete(productInfoDTO.toVO()))
    }

    @PostMapping("/brand/save")
    fun saveBrand(@RequestBody saveBrandDTO: SaveBrandDTO): ResponseDTO<Boolean> {

        return ResponseDTO.success(saveBrandUseCase.saveBrand(saveBrandDTO.toVO()))
    }
}