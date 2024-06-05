package com.sinsa.response.enum

enum class ExceptionCode(
    val message: String
) {
    PRODUCT_NOT_FOUND("상품 정보를 찾을 수 없습니다."),
    PRODUCT_UPDATE_FAIL("상품 정보 업데이트에 실패 하였습니다."),
    PRODUCT_SAVE_FAIL("상품 정보 저장에 실패 하였습니다."),
    PRODUCT_CATEGORY_BRAND_NOT_ENOUGH("해당 브랜드로 등록된 카테고리 상품이 충분치 않아 삭제할 수 없습니다.")
}