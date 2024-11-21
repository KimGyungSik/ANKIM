package shoppingmall.ankim.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.service.request.ProductImgDto;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.exception.DetailImageRequiredException;
import shoppingmall.ankim.domain.image.exception.FileUploadException;
import shoppingmall.ankim.domain.image.exception.ImageLimitExceededException;
import shoppingmall.ankim.domain.image.exception.ThumbnailImageRequiredException;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
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

    public void updateProductImgs(Long productId, ProductImgUpdateServiceRequest request) {
        Product product = getProductWithImgs(productId);

        // 요청에서 제공된 이미지 URL 리스트
        List<String> newThumbnailUrls = extractUrls(request.getThumbnailImages());
        List<String> newDetailUrls = extractUrls(request.getDetailImages());

        // 기존 썸네일 및 상세 이미지 분리
        List<ProductImg> existingThumbnails = product.getProductImgs().stream()
                .filter(img -> THUMBNAIL.equals(img.getRepimgYn()))
                .toList();

        List<ProductImg> existingDetails = product.getProductImgs().stream()
                .filter(img -> DETAIL.equals(img.getRepimgYn()))
                .toList();

        // 1. 삭제: 요청에 없는 기존 이미지를 삭제
        deleteImages(product, existingThumbnails, newThumbnailUrls);
        deleteImages(product, existingDetails, newDetailUrls);

        // 2. 추가: 요청에 있는 새로운 이미지를 추가
        saveNewImages(product, request.getThumbnailImages(), THUMBNAIL, existingThumbnails, newThumbnailUrls);
        saveNewImages(product, request.getDetailImages(), DETAIL, existingDetails, newDetailUrls);
    }

    private void deleteImages(Product product, List<ProductImg> existingImgs, List<String> newImgUrls) {
        // 삭제 대상 이미지 리스트를 필터링
        List<ProductImg> imgsToDelete = existingImgs.stream()
                .filter(img -> !newImgUrls.contains(img.getOriImgName())) // 요청에 없는 이미지
                .toList();

        for (ProductImg img : imgsToDelete) {
            // 상품의 이미지 리스트에서 제거
            product.removeProductImg(img);
            // 기존 삭제 메서드 활용 (DB와 스토리지에서 삭제)
            deleteProductImg(img);
        }
    }


    private void saveNewImages(Product product, List<MultipartFile> newImages, String repimgYn,
                               List<ProductImg> existingImgs, List<String> newImgUrls) {
        if (newImages == null || newImages.isEmpty()) return;

        for (int i = 0; i < newImages.size(); i++) {
            MultipartFile imageFile = newImages.get(i);
            String oriImgName = imageFile.getOriginalFilename();

            // 이미 존재하는 이미지가 아니라면 추가
            if (existingImgs.stream().noneMatch(img -> img.getOriImgName().equals(oriImgName))) {
                ProductImg productImg = ProductImg.init(product, repimgYn, i + 1);
                saveProductImg(productImg, imageFile); // 기존 저장 메서드 활용
                product.addProductImg(productImg);
            }
        }
    }

    private List<String> extractUrls(List<MultipartFile> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(MultipartFile::getOriginalFilename) // 원본 파일명 사용
                .toList();
    }


    private void deleteProductImg(ProductImg productImg) {
        // 로컬 파일 삭제
        if (productImg.getImgName() != null && !productImg.getImgName().isEmpty()) {
            fileService.deleteFile(itemImgLocation + "/" + productImg.getImgName());
        }

        // S3 파일 삭제
        if (productImg.getImgUrl() != null && !productImg.getImgUrl().isEmpty()) {
            s3Service.deleteFile(productImg.getImgUrl().substring(productImg.getImgUrl().lastIndexOf("/") + 1));
        }

        // 데이터베이스에서 삭제
        productImgRepository.delete(productImg);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }

    private Product getProductWithImgs(Long productId) {
        return productRepository.findByIdWithProductImgs(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }

    private void validateImageCounts(ProductImgDto request) {
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


