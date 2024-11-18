package shoppingmall.ankim.domain.image.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public abstract class ProductImgDto {
    private List<MultipartFile> thumbnailImages;

    private List<MultipartFile> detailImages;

    private ProductImgDto(List<MultipartFile> thumbnailImages, List<MultipartFile> detailImages) {
        this.thumbnailImages = thumbnailImages;
        this.detailImages = detailImages;
    }
}
