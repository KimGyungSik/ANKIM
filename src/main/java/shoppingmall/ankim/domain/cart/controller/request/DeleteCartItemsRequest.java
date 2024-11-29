package shoppingmall.ankim.domain.cart.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

// 장바구니 삭제용 request dto
@Data
@NoArgsConstructor
public class DeleteCartItemsRequest {

    @NotNull
    private Long cartItemNo;

    @NotBlank
    private ProductSellingStatus sellingStatus;


}