package shoppingmall.ankim.domain.product.dto;

import lombok.Getter;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductResponse {
    private final Long no;
    private final String name;
    private final String code;
    private final String desc;
    private final Integer discRate;
    private final Integer sellPrice;
    private final Integer origPrice;
    private final String optYn;
    private final String restockYn;
    private final Integer qty;
    private final String sellingStatus;
    private final String bestYn;
    private final String freeShip;
    private final Integer shipFee;
    private final String searchKeywords;
    private final String relProdCode;
    private final String cauProd;
    private final String cauOrd;
    private final String cauShip;
    private final Integer avgR;
    private final Integer wishCnt;
    private final Integer viewCnt;
    private final Integer rvwCnt;
    private final Integer qnaCnt;
    private final Integer dispOrd;

    // Related entities as nested DTOs
    private final CategoryResponse categoryResponse;
    private final List<ProductImgResponse> productImgs;
    private final List<OptionGroupResponse> optionGroups;
    private final List<ItemResponse> items;

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
        this.sellingStatus = product.getSellingStatus().name();
        this.bestYn = product.getBestYn();
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
