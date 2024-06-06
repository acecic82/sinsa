package com.sinsa.response.enum

enum class ExceptionCode(
    val message: String
) {
    //PRODUCTION
    PRODUCT_NOT_FOUND("상품 정보를 찾을 수 없습니다."),
    PRODUCT_UPDATE_FAIL("상품 정보 업데이트에 실패 하였습니다."),
    PRODUCT_SAVE_FAIL("상품 정보 저장에 실패 하였습니다."),
    PRODUCT_DELETE_FAIL("삭제할 대상이 없습니다."),
    PRODUCT_CATEGORY_BRAND_NOT_ENOUGH("해당 브랜드로 등록된 카테고리 상품이 충분치 않아 삭제할 수 없습니다."),
    PRODUCT_CATEGORY_BRAND_NOT_ENOUGH_UPDATE("해당 브랜드로 등록된 카테고리 상품이 충분치 않아 수정할 수 없습니다."),

    //BRAND
    BRAND_NOT_EXIST("브랜드가 존재하지 않습니다."),
    //브랜드를 새로 추가하는 경우 필요합니다.
    BRAND_ALREADY_EXIST("이미 존재하는 브랜드 입니다."),

    //CATEGORY
    CATEGORY_NOT_EXIST("존재하지 않는 카테고리 입니다.")
}