package shoppingmall.ankim.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.exception.DetailImageRequiredException;
import shoppingmall.ankim.domain.image.exception.FileUploadException;
import shoppingmall.ankim.domain.image.exception.ImageLimitExceededException;
import shoppingmall.ankim.domain.image.exception.ThumbnailImageRequiredException;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ProductImgRepository productImgRepository;
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final S3Service s3Service;  // S3Service 주입

    private static final String THUMBNAIL = "Y";
    private static final String DETAIL = "N";

    public void createProductImgs(Long productId, ProductImgCreateServiceRequest request) {
        Product product = getProduct(productId);
        validateImageCounts(request);

        // 썸네일 이미지 및 상세 이미지들을 저장
        saveImages(product, request.getThumbnailImages(), THUMBNAIL);
        saveImages(product, request.getDetailImages(), DETAIL);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }

    private void validateImageCounts(ProductImgCreateServiceRequest request) {
        if (request.getThumbnailImages() == null || request.getThumbnailImages().isEmpty()) {
            throw new ThumbnailImageRequiredException(THUMBNAIL_IMAGE_REQUIRED);
        }
        if (request.getThumbnailImages().size() > 6) {
            throw new ImageLimitExceededException(IMAGE_LIMIT_EXCEEDED);
        }
        if (request.getDetailImages() == null || request.getDetailImages().isEmpty()) {
            throw new DetailImageRequiredException(DETAIL_IMAGE_REQUIRED);
        }
        if (request.getDetailImages().size() > 6) {
            throw new ImageLimitExceededException(IMAGE_LIMIT_EXCEEDED);
        }
    }

    private void saveImages(Product product, List<MultipartFile> images, String repimgYn) {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile imageFile = images.get(i);
            ProductImg productImg = ProductImg.init(product, repimgYn, i + 1);
            saveProductImg(productImg, imageFile);
            product.addProductImg(productImg);
        }
    }

    private void saveProductImg(ProductImg productImg, MultipartFile itemImgFile) {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (oriImgName != null && !oriImgName.isEmpty()) {
            try {
                // S3 업로드
                String s3FileName = s3Service.uploadSingle(itemImgFile);  // S3Service에서 단일 파일 업로드 추가
                imgUrl = s3FileName;  // S3 URL로 설정

                // 로컬 업로드
                imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            } catch (IOException e) {
                throw new FileUploadException(FILE_UPLOAD_FAIL);
            }
        }

        productImg.updateProductImg(oriImgName, imgName, imgUrl);
        productImgRepository.save(productImg);
    }
}


