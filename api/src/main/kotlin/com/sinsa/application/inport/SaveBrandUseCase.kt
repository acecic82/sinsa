package com.sinsa.application.inport

import com.sinsa.application.vo.SaveBrandVO

interface SaveBrandUseCase {
    fun saveBrand(saveBrandVO: SaveBrandVO): Boolean
}