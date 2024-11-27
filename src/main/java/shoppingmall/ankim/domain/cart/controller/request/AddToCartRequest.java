package shoppingmall.ankim.domain.cart.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;

import java.util.List;

// 장바구니 데이터 추가
@Data
@NoArgsConstructor
public class AddToCartRequest {
    /*
     * 상품 번호 : ProductUserDetailResponse
     * */
    @NotNull
    private Long productNo;

    /*
    * 옵션값 번호 : OptionValueResponse
    * ItemOption엔티티의 optionValueNo를 조건으로 검색하여 ItemNo를 추출하기 위해 사용
    * */
    @NotNull
    private List<Long> optionValueNoList;

    /*
     * 대표 상품 이미지 경로 -> CartItem
     * */

    /*
    * 구매수량 -> CartItem
    * */
    @NotNull
    private Integer qty;

    public AddToCartServiceRequest toServiceRequest() {
        return AddToCartServiceRequest.builder()
                .productNo(productNo)
                .optionValueNoList(optionValueNoList)
                .qty(qty)
                .build();
    }


}