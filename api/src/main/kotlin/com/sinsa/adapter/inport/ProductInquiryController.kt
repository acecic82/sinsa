package com.sinsa.adapter.inport

import com.sinsa.adapter.dto.LowHighBrandInfoDTO
import com.sinsa.adapter.dto.LowestBrandInfoDTO
import com.sinsa.adapter.dto.LowestCategoryInfoDTO
import com.sinsa.application.inport.FindLowAndHighBrandUseCase
import com.sinsa.application.inport.FindLowestProductUseCase
import com.sinsa.common.response.SuccessResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductInquiryController(
    private val findLowestProductUseCase: FindLowestProductUseCase,
    private val findLowAndHighBrandUseCase: FindLowAndHighBrandUseCase
) {

    @GetMapping("/lowest/all-category")
    fun getLowestPriceAllCategory(): SuccessResponseDTO<LowestCategoryInfoDTO> {
        return SuccessResponseDTO.success(LowestCategoryInfoDTO.from(findLowestProductUseCase.findLowestPriceAllCategory()))
    }

    @GetMapping("/lowest/brand")
    fun getLowestPriceBrand(): SuccessResponseDTO<List<LowestBrandInfoDTO>> {
        return SuccessResponseDTO.success(findLowestProductUseCase.findLowestPriceAllBrand().groupBy { it.brand }.map {
            LowestBrandInfoDTO.fromBrand(it.key, it.value)
        })
    }

    @GetMapping("/low-high/brand/{category}")
    fun getLowAndHighCategory(@PathVariable category: String): SuccessResponseDTO<LowHighBrandInfoDTO> {
        return SuccessResponseDTO.success(findLowAndHighBrandUseCase.findLowAndHighBrand(category).let {
            LowHighBrandInfoDTO.from(it)
        })
    }
}