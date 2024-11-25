package shoppingmall.ankim.domain.image.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.image.entity.ProductImg;

@Data
@NoArgsConstructor
public class ProductImgUrlResponse {
    private String imgUrl;
    private String repImgYn;
    private Integer ord;

    @Builder
    private ProductImgUrlResponse(String imgUrl, String repImgYn, Integer ord) {
        this.imgUrl = imgUrl;
        this.repImgYn = repImgYn;
        this.ord = ord;
    }

    public static ProductImgUrlResponse of(ProductImg itemImg) {
        return ProductImgUrlResponse.builder()
                .imgUrl(itemImg.getImgUrl())
                .repImgYn(itemImg.getRepimgYn())
                .ord(itemImg.getOrd())
                .build();
    }
}
