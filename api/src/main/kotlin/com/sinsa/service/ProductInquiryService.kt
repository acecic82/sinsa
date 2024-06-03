package com.sinsa.service

import com.sinsa.application.inport.FindLowAndHighBrandUseCase
import com.sinsa.application.inport.FindLowestProductUseCase
import com.sinsa.application.outport.FindProductPort
import com.sinsa.application.vo.LowHighBrandInfoVO
import com.sinsa.application.vo.ProductInfoVO
import com.sinsa.entity.Product.Companion.BRAND_SUM_PRICE_LIMIT
import com.sinsa.entity.Product.Companion.ORDER_PRICE_LIMIT
import org.springframework.stereotype.Service


@Service
class ProductInquiryService(
    private val findProductPort: FindProductPort
): FindLowestProductUseCase, FindLowAndHighBrandUseCase {
    override fun findLowestPriceAllCategory(): List<ProductInfoVO> {
        return findProductPort.findLowestCategoryList()
    }

    override fun findLowestPriceAllBrand(): List<ProductInfoVO> {
        //최저가를 찾기 위해 orderBy 이후에 limit 1을 하면 가능하지만
        //이렇게 안한 이유는 브랜드 별로 합한 금액이 여러개인 경우 1개만 찾게되는 문제가 있습니다
        //그래서 우선 리스트를 다 가져옵니다. 10개만 가져오는 이유는 모든 리스트를 가져오게 되면
        //서버 메모리에 올라오게 되는데 brand 가 너무 많을 경우 서버 리소스에 영향이 가기 떄문에 적당한 값만
        //limit 을 이용하여 가져오게 됩니다.
        //한 가지 맹정믄 만약 sum 한 가격이 같은 최저가 브랜드가 10개를 넘어갈 경우 10개 이후에 나오는 브랜드는
        //항목에 포함이 될 수 없습니다. 이럴 경우엔 limit 의 적절한 값을 조절하여(business logic) 이 부분을 해결하면 됩니다.
        //business logic 이기 때문에 BRAND_SUM_PRICE_LIMIT 는 domain 에 있게 됩니다.
        val brandInfoList = findProductPort.findAllBrandSumPrice(BRAND_SUM_PRICE_LIMIT)

        // 가져온 리스트 중 최저가를 찾아냅니다.
        val lowestPriceBrand = brandInfoList.minBy { it.price }

        // 리스트에서 최저가와 일치하는 brand list 를 뽑아냅니다.
        val lowestPriceBrandList =
            brandInfoList.filter { it.price == lowestPriceBrand.price }.map { it.brand }

        // 최저가 리스트로 상품에서 검색해서 리스트를 반환합니다.
        return findProductPort.findBrandProductList(lowestPriceBrandList)
    }

    override fun findLowAndHighBrand(category: String): LowHighBrandInfoVO {
        //Order by 후 limit 을 이용하여 최대 최소를 찾기 위한 리스트를 받아옵니다.
        //변수명이 candidate 인 이유는 limit 된 값이 1이 아닌이상 최소를 보장하지 않기 때문입니다.
        val lowestListCandidate = findProductPort.findLowestListByCategory(category, ORDER_PRICE_LIMIT)
        val highestListCandidate = findProductPort.findHighestListByCategory(category, ORDER_PRICE_LIMIT)

        //최대, 최소 값을 찾습니다.
        val lowestBrand = lowestListCandidate.minBy { it.price }
        val highestBrand = highestListCandidate.maxBy { it.price }

        //최대, 최소와 일치하는 값을 찾아서 리스트로 만들어줍니다.
        //최대, 최소이지만 리스트인 이유는 최대, 최소가 다수일 경우도 보여주기 위해서 입니다.
        //limit 보다 동등한 개수가 많은 경우는 부정확할 수 있지만, 동등한것이 많은 경우에도 10개 정도면 충분히 보여주고 있다고 판단하여
        //limit 만큼 보여주는 로직입니다.
        //만약 business logic 이 바뀌게 되면 limit 만 수만 바꿔주면 되기 때문에 코드 수정도 용이합니다.
        val lowestList = lowestListCandidate.filter { lowestBrand.price == it.price }
        val highestList = highestListCandidate.filter { highestBrand.price == it.price }

        return LowHighBrandInfoVO(category, lowestList, highestList)
    }
}