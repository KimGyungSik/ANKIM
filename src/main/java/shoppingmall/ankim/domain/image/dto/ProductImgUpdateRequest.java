package shoppingmall.ankim.domain.image.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.image.validation.ValidImageFile;

import java.util.List;

public class ProductImgUpdateRequest {
    @NotEmpty(message = "썸네일 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> thumbnailImages;

    @NotEmpty(message = "상세 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> detailImages;

    @Builder
    private ProductImgUpdateRequest(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
    public ProductImgUpdateServiceRequest toServiceRequest() {
        return ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(this.thumbnailImages)
                .detailImages(this.detailImages)
                .build();
    }
}
