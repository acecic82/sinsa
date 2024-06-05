package com.sinsa.entity

import jakarta.persistence.*


@Entity
@Table(name = "brand")
data class BrandEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val brand: String
) {
    constructor() : this(null, "")

}
