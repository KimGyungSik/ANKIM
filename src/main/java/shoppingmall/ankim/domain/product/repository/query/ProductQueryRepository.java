package shoppingmall.ankim.domain.product.repository.query;

import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;

public interface ProductQueryRepository {
    // 상품 상세페이지 조회 by User (상품 기본 필드, 상품 이미지, 옵션 그룹 및 옵션 값)
    ProductUserDetailResponse findProductUserDetailResponse(Long productId);

    // 상품 수정 페이지 by Admin (모든 필드를 보여줘야함)
    ProductResponse adminDetailProduct(Long productId);
}
