package shoppingmall.ankim.domain.image.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.image.entity.ProductImg;

@Data
@NoArgsConstructor
public class ProductImgResponse {
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private Integer ord;

    @Builder
    private ProductImgResponse(Long id, String imgName, String oriImgName, String imgUrl, String repImgYn, Integer ord) {
        this.id = id;
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.repImgYn = repImgYn;
        this.ord = ord;
    }

    public static ProductImgResponse of(ProductImg itemImg) {
        return ProductImgResponse.builder()
                .id(itemImg.getNo())
                .imgName(itemImg.getImgName())
                .oriImgName(itemImg.getOriImgName())
                .imgUrl(itemImg.getImgUrl())
                .repImgYn(itemImg.getRepimgYn())
                .ord(itemImg.getOrd())
                .build();
    }
}
