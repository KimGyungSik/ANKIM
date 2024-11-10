package shoppingmall.ankim.domain.image.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductImgeServiceRequest {
    private List<MultipartFile> thumbnailImages;

    private List<MultipartFile> detailImages;

    @Builder
    private ProductImgeServiceRequest(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
}
