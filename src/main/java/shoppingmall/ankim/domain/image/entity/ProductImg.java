package shoppingmall.ankim.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.product.entity.Product;
/*
 * 상품 이미지 정책
    * 썸네일 이미지, 상세 이미지 1장은 필수로 등록해야함
    * 최대 6장까지 등록 가능
    * 첫 번째 이미지가 대표이미지로 설정됨
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ProductImg")
public class ProductImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    private String imgName; // 이미지 파일명, 실제 로컬에 저장된 상품 이미지 파일의 이름

    @Column(name = "orig_name")
    private String oriImgName; // 원본 이미지 파일명, 업로드했던 상품 이미지 파일의 원래 이름

    private String imgUrl; // 이미지 조회 경로, 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
    private String repimgYn; // 대표 이미지 여부 (Y: 썸네일 / N: 상세)
    private Integer ord; // 이미지 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product;

    @Builder
    private ProductImg(String imgName, String oriImgName, String imgUrl, String repimgYn, Integer ord, Product product) {
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.repimgYn = repimgYn;
        this.ord = ord;
        this.product = product;
    }

    public static ProductImg create(String imgName, String oriImgName, String imgUrl, String repimgYn, Integer ord, Product product) {
        return ProductImg.builder()
                .imgName(imgName)
                .oriImgName(oriImgName)
                .imgUrl(imgUrl)
                .repimgYn(repimgYn)
                .ord(ord)
                .product(product)
                .build();
    }

    public static ProductImg init(Product product,String repimgYn, Integer ord) {
        return ProductImg.builder()
                .repimgYn(repimgYn)
                .ord(ord)
                .product(product)
                .build();
    }

    public void updateProductImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
