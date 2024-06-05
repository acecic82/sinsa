package com.sinsa.application.outport

interface FindBrandPort {
    fun findExistBrand(brand: String): String?
}