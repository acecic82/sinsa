# Sinsa-App

카테고리와 브랜드의 가격을 조회 및 구매 할 수 있는 앱


### 주요기능

1. 고객은 카테고리 별로 최저가격인 브랜드를 조회하고 총액이 얼마인지 알 수 있어야 한다.
2. 고객은 단일 브랜로 전체 카테고리 상품을 구매할 경우 최저 가격인 브랜드와 총액이 얼마인지 확인할 수 있어야 한다.
3. 고객은 특정 카테고리에서 최저가격 브랜드와 고가격 브랜드를 확인하고 각 브랜드의 상품의 가격을 확인할 수 있어야 한다.
4. 운영자는 새로운 브랜드를 등록하고, 모든 브랜드의 상품을 추가, 변경, 삭제 할 수 있어야 한다.


### 구조

헥사고날 아키텍처를 기반으로 설계

### 모듈

API : Controller, UseCase, Service, Port 의 형태로 처리하는 모듈

Domain : Business logic 처리를 위한 entity를 저장하고 있는 모듈

Persistence : DB설정 및 DB entity가 저장되어있는 모듈

### 의존 관계

API -> Domain, Persistence

Persistence -> Domain

### API

1. 카테고리 별 최저가 브랜드와 삼품 가격, 총액을 조회하는 API
- detail : 최저 가격의 브랜드가 여러개인 경우는 여러개를 출력한다.
```
request : 없음
Response : 
{
    categoryAndBrandInfoVOList: List<ProductInfoVO>,
    totalPrice: BigDecimal
}
```

2. 단일 브랜드로 모든 카테고리 상품을 구매할 때, 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
- detail : 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격을 찾는 브랜드가 여러 개인 경우 여러개를 리스트 형태로 반환한다.
```
request : 없음
response : 
{
    [
        {
            brand: String,
            categoryAndPriceList: List<CategoryAndPriceVO>,
            totalPrice: BigDecimal
        }
    ]
}
```

3. 카테고리 이름으로 최저, 최고 가격의 브랜드와 상품 가격을 조회하는 API
- detail : 카테고리 이름에서 최저 혹은 최고 가격의 브랜드가 여러개인 경우 여러개를 보여준다.
```
request: category name
response : 
{
    category: String,
    lowestBrandList: List<BrandAndPriceVO>,
    highestBrandList: List<BrandAndPriceVO>
}
```

4. 브랜드 및 상품을 추가/삭제/업데이트 하는 API

- Delete
```
request
{
    productId: Long? = null,
    category: String,
    brand: String,
    price: BigDecimal
}

response : Boolean
```
detail : id 가 있다면 id를 기준으로 삭제하고 id가 없다면 category, brand, price 가 일치하는 경우 삭제한다.

- Update

```
request
{
    productId: Long? = null,
    category: String,
    brand: String,
    price: BigDecimal
}

response : Boolean
```

detail : id 가 없다면 업데이트를 할 수 없다.

  - Save
```
request
{
    productId: Long? = null,
    category: String,
    brand: String,
    price: BigDecimal
}

response : Boolean
```

### DB

```
CREATE TABLE product (
    id bigint generated by default as identity,
    brand varchar(255),
    category varchar(255),
    price numeric(38,2),
    primary key (id)
);

create INDEX idx_brand on product(brand);
create INDEX idx_category on product(category);
create INDEX idx_price on product(price);
```
detail : group by, order by 시 쿼리 속도의 상승을 위해 index 추가