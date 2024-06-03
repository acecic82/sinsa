package com.sinsa.response.enum

enum class ExceptionCode(
    val message: String
) {
    PRODUCT_NOT_FOUND("상품 정보를 찾을 수 없습니다."),
    PRODUCT_UPDATE_FAIL("상품 정보 업데이트에 실패 하였습니다."),
    PRODUCT_SAVE_FAIL("상품 정보 저장에 실패 하였습니다."),
    PRODUCT_NOT_FOUND_CATEGORY("상품의 카테고리 정보를 찾을 수 없습니다.")
}