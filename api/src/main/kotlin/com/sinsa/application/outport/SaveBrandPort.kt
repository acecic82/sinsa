package com.sinsa.application.outport

interface SaveBrandPort {
    fun save(id: Long?, brand: String) : String
}