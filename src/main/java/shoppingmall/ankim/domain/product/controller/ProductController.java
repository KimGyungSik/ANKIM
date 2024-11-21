package shoppingmall.ankim.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.dto.ProductImgUpdateRequest;
import shoppingmall.ankim.domain.image.dto.ProductImgeCreateRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductCreateRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductUpdateRequest;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.service.ProductService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping(value = "/api/v1/products/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> createProduct(
            @RequestPart("productCreateRequest") @Valid ProductCreateRequest productCreateRequest,
            @RequestPart("thumbnailImages") List<MultipartFile> thumbnailImages,
            @RequestPart("detailImages") List<MultipartFile> detailImages
    ) {
        ProductImgeCreateRequest productImages = ProductImgeCreateRequest.builder()
                .thumbnailImages(thumbnailImages)
                .detailImages(detailImages)
                .build();
        productCreateRequest.setProductImages(productImages);

        return ApiResponse.ok(productService.createProduct(productCreateRequest.toServiceRequest()));
    }

    // 상품 수정
    @PutMapping(value = "/api/v1/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestPart("productUpdateRequest") @Valid ProductUpdateRequest productUpdateRequest,
            @RequestPart("thumbnailImages") List<MultipartFile> thumbnailImages,
            @RequestPart("detailImages") List<MultipartFile> detailImages
    ) {
        // 상품 이미지 처리
        ProductImgUpdateRequest productImages = ProductImgUpdateRequest.builder()
                .thumbnailImages(thumbnailImages)
                .detailImages(detailImages)
                .build();
        productUpdateRequest.setProductImages(productImages);

        // 서비스 요청 처리
        return ApiResponse.ok(productService.updateProduct(productId, productUpdateRequest.toServiceRequest()));
    }






}
