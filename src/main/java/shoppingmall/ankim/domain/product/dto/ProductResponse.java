package shoppingmall.ankim.domain.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductResponse {
    private Long no;
    private String name;
    private String code;
    private String desc;
    private Integer discRate;
    private Integer sellPrice;
    private Integer origPrice;
    private String optYn;
    private String restockYn;
    private Integer qty;
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
    private Integer dispOrd;

    // Related entities as nested DTOs
    private CategoryResponse categoryResponse;
    private List<ProductImgResponse> productImgs;
    private List<OptionGroupResponse> optionGroups;
    private List<ItemResponse> items;

    private ProductResponse(Product product) {
        this.no = product.getNo();
        this.name = product.getName();
        this.code = product.getCode();
        this.desc = product.getDesc();
        this.discRate = product.getDiscRate();
        this.sellPrice = product.getSellPrice();
        this.origPrice = product.getOrigPrice();
        this.optYn = product.getOptYn();
        this.restockYn = product.getRestockYn();
        this.qty = product.getQty();
        this.sellingStatus = product.getSellingStatus();
        this.handMadeYn = product.getHandMadeYn();
        this.freeShip = product.getFreeShip();
        this.shipFee = product.getShipFee();
        this.searchKeywords = product.getSearchKeywords();
        this.relProdCode = product.getRelProdCode();
        this.cauProd = product.getCauProd();
        this.cauOrd = product.getCauOrd();
        this.cauShip = product.getCauShip();
        this.avgR = product.getAvgR();
        this.wishCnt = product.getWishCnt();
        this.viewCnt = product.getViewCnt();
        this.rvwCnt = product.getRvwCnt();
        this.qnaCnt = product.getQnaCnt();
        this.dispOrd = product.getDispOrd();

        this.categoryResponse = CategoryResponse.of(product.getCategory());
        this.productImgs = product.getProductImgs().stream()
                .map(ProductImgResponse::of)
                .collect(Collectors.toList());
        this.optionGroups = product.getOptionGroups().stream()
                .map(OptionGroupResponse::of)
                .collect(Collectors.toList());
        this.items = product.getItems().stream()
                .map(ItemResponse::of)
                .collect(Collectors.toList());
    }

    public static ProductResponse of(Product product) {
        return new ProductResponse(product);
    }
}
