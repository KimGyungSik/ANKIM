package shoppingmall.ankim.domain.image.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.validation.ValidImageFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductImgeRequest {
    @NotEmpty(message = "썸네일 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> thumbnailImages;

    @NotEmpty(message = "상세 이미지는 필수입니다.")
    @ValidImageFile
    private List<MultipartFile> detailImages;

    @Builder
    private ProductImgeRequest(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
}
