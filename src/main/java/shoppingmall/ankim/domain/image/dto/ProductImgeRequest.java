package shoppingmall.ankim.domain.image.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductImgeRequest {
    // 썸네일 이미지 리스트 (1장 필수)
    @NotEmpty(message = "썸네일 이미지는 필수입니다.")
    @Size(max = 6, message = "썸네일 이미지는 최대 6장까지 업로드할 수 있습니다.")
    private List<MultipartFile> thumbnailImages;

    // 상세 이미지 리스트 (최대 6장)
    @NotEmpty(message = "썸네일 이미지는 필수입니다.")
    @Size(max = 6, message = "상세 이미지는 최대 6장까지 업로드할 수 있습니다.")
    private List<MultipartFile> detailImages;

    @Builder
    private ProductImgeRequest(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
}
