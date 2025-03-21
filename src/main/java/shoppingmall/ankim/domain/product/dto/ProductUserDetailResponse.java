package shoppingmall.ankim.domain.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

// 상품 상세 페이지 DTO by User
@Data
@NoArgsConstructor
public class ProductUserDetailResponse {
    private Long no;
    private String name;
    private String code;
    private String desc;
    private Integer discRate;
    private Integer sellPrice;
    private Integer origPrice;
    private ProductSellingStatus sellingStatus;
    private String handMadeYn;
    private String freeShip;
    private Integer shipFee;
    private String searchKeywords;
    private String relProdCode;
    private String cauProd;
    private String cauOrd;
    private String cauShip;
    private Integer avgR;
    private Integer wishCnt;
    private Integer viewCnt;
    private Integer rvwCnt;
    private Integer qnaCnt;
    // FIXME 추가금액 필드도 추가해야할듯 Item.addPrice
    private CategoryResponse categoryResponse;
    private List<ProductImgUrlResponse> productImgs;
    private List<OptionGroupResponse> optionGroups;
}
