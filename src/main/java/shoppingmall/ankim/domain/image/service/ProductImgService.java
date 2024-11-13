package shoppingmall.ankim.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    private final static String thumbnail = "Y";
    private final static String detail = "N";

    public void createProductImgs(Product product, ProductImgCreateServiceRequest request)  {
        validateImageCounts(request);

        saveImages(product, request.getThumbnailImages(), thumbnail);
        saveImages(product, request.getDetailImages(), detail);
    }

    // 이미지 개수 검사를 위한 메서드
    private void validateImageCounts(ProductImgCreateServiceRequest request) {
        // 썸네일 이미지 개수 검사
        if (request.getThumbnailImages() == null || request.getThumbnailImages().isEmpty()) {
            throw new ThumbnailImageRequiredException(THUMBNAIL_IMAGE_REQUIRED);
        }
        if (request.getThumbnailImages().size() > 6) {
            throw new ImageLimitExceededException(IMAGE_LIMIT_EXCEEDED);
        }

        // 상세 이미지 개수 검사
        if (request.getDetailImages() == null || request.getDetailImages().isEmpty()) {
            throw new DetailImageRequiredException(DETAIL_IMAGE_REQUIRED);
        }
        if (request.getDetailImages().size() > 6) {
            throw new ImageLimitExceededException(IMAGE_LIMIT_EXCEEDED);
        }
    }

    // 이미지 리스트 저장 메서드
    private void saveImages(Product product, List<MultipartFile> images, String repimgYn)  {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile imageFile = images.get(i);
            ProductImg productImg = ProductImg.init(product,repimgYn, i + 1);
            saveProductImg(productImg, imageFile);

            // 상품에 이미지 추가
            product.addProductImg(productImg);
        }
    }

    // 개별 이미지 파일 저장 메서드
    private void saveProductImg(ProductImg productImg, MultipartFile itemImgFile)  {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            try {
                imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            } catch (IOException e) {
                throw new FileUploadException(FILE_UPLOAD_FAIL);
            }
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 업데이트 및 저장
        productImg.updateProductImg(oriImgName, imgName, imgUrl);
        productImgRepository.save(productImg);
    }
}

