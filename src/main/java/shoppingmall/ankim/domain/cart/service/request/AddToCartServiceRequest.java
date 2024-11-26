package shoppingmall.ankim.domain.cart.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 장바구니 데이터 추가
@Data
@NoArgsConstructor
public class AddToCartServiceRequest {

    private Long productNo;

    private List<Long> optionValueNoList;

    private String thumbNailImgUrl;

    private Integer qty;

    @Builder
    public AddToCartServiceRequest(Long productNo, List<Long> optionValueNoList, String thumbNailImgUrl, Integer qty) {
        this.productNo = productNo;
        this.optionValueNoList = optionValueNoList;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.qty = qty;
    }
}