package shoppingmall.ankim.domain.image.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.validation.ValidImageFile;

import java.util.List;

// TODO 상품 등록 시 필드 validator 테스트 진행해야함
@Getter
@NoArgsConstructor
public class ProductImgeCreateRequest {
    @NotEmpty(message = "썸네일 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> thumbnailImages;

    @NotEmpty(message = "상세 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> detailImages;

    @Builder
    private ProductImgeCreateRequest(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
    public ProductImgCreateServiceRequest toServiceRequest() {
        return ProductImgCreateServiceRequest.builder()
                .thumbnailImages(this.thumbnailImages)
                .detailImages(this.detailImages)
                .build();
    }
}
