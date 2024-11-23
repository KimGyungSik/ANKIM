package shoppingmall.ankim.domain.product.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.product.entity.Product;

import java.time.LocalDateTime;

/*
    * 썸네일 이미지 (첫번째 사진)
    * 평균별점
*/


@Data
@NoArgsConstructor
public class ProductListResponse {
    private Long no; // 상품ID
    private String categoryName; // 카테고리 이름
    private String thumbNailImgUrl; // 썸네일 이미지 (ord=1)
    private String name; // 상품명
    private String code; // 상품코드
    private Integer discRate; // 할인율
    private Integer sellPrice; // 상품가격
    private LocalDateTime createdAt; // 상품 등록일
    private String handMadeYn; // 핸드메이드 여부
    private String freeShip; // 무료배송 여부
    private Integer wishCnt; // 찜 갯수
    private Integer rvwCnt; // 리뷰 갯수
    private Integer avgR; // 평균별점

    @Builder
    private ProductListResponse(Long no, String categoryName, String thumbNailImgUrl, String name, String code, Integer discRate, Integer sellPrice, LocalDateTime createdAt, String handMadeYn, String freeShip, Integer wishCnt, Integer rvwCnt, Integer avgR) {
        this.no = no;
        this.categoryName = categoryName;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.name = name;
        this.code = code;
        this.discRate = discRate;
        this.sellPrice = sellPrice;
        this.createdAt = createdAt;
        this.handMadeYn = handMadeYn;
        this.freeShip = freeShip;
        this.wishCnt = wishCnt;
        this.rvwCnt = rvwCnt;
        this.avgR = avgR;
    }

    public static ProductListResponse of(Product product) {
        return ProductListResponse.builder()
                .no(product.getNo())
                .name(product.getName())
                .code(product.getCode())
                .discRate(product.getDiscRate())
                .sellPrice(product.getSellPrice())
                .createdAt(product.getCreatedAt())
                .handMadeYn(product.getHandMadeYn())
                .freeShip(product.getFreeShip())
                .wishCnt(product.getWishCnt())
                .rvwCnt(product.getRvwCnt())
                .avgR(product.getAvgR())
                .build();
    }
}
