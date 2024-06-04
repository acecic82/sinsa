package com.sinsa.adapter.inport

import com.sinsa.adapter.dto.LowHighBrandInfoDTO
import com.sinsa.adapter.dto.LowestBrandInfoDTO
import com.sinsa.adapter.dto.LowestCategoryInfoDTO
import com.sinsa.adapter.dto.ProductInfoDTO
import com.sinsa.application.inport.FindAllProductUseCase
import com.sinsa.application.inport.FindLowAndHighBrandUseCase
import com.sinsa.application.inport.FindLowestProductUseCase
import com.sinsa.common.response.ResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductInquiryController(
    private val findLowestProductUseCase: FindLowestProductUseCase,
    private val findLowAndHighBrandUseCase: FindLowAndHighBrandUseCase,
    private val findAllProductUseCase: FindAllProductUseCase
) {
    @GetMapping("/all")
    fun getProductAll(): ResponseDTO<List<ProductInfoDTO>> {
        val dtoList = findAllProductUseCase.findAllProduct().map {
            ProductInfoDTO.from(it)
        }
        return ResponseDTO.success(dtoList)
    }

    @GetMapping("/lowest/all-category")
    fun getLowestPriceAllCategory(): ResponseDTO<LowestCategoryInfoDTO> {
        return ResponseDTO.success(LowestCategoryInfoDTO.from(findLowestProductUseCase.findLowestPriceAllCategory()))
    }

    @GetMapping("/lowest/brand")
    fun getLowestPriceBrand(): ResponseDTO<List<LowestBrandInfoDTO>> {
        return ResponseDTO.success(findLowestProductUseCase.findLowestPriceAllBrand().groupBy { it.brand }.map {
            LowestBrandInfoDTO.fromBrand(it.key, it.value)
        })
    }

    @GetMapping("/low-high/brand/{category}")
    fun getLowAndHighCategory(@PathVariable category: String): ResponseDTO<LowHighBrandInfoDTO> {
        return ResponseDTO.success(findLowAndHighBrandUseCase.findLowAndHighBrand(category).let {
            LowHighBrandInfoDTO.from(it)
        })
    }
}