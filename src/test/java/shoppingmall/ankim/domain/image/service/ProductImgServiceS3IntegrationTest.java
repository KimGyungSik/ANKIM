package shoppingmall.ankim.domain.image.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional

class ProductImgServiceS3IntegrationTest {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private ProductImgRepository productImgRepository;

    @Autowired
    private ProductRepository productRepository;

    private final List<String> uploadedFileNames = new ArrayList<>();

    @Test
    @DisplayName("AWS S3에 파일이 실제로 업로드되고 URL을 반환한다.")
    void uploadFileToS3() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile(
                "test-image", "test-image.jpg", "image/jpeg", "test data".getBytes()
        );

        // when
        String s3Url = s3Service.uploadSingle(file);

        // S3 URL에서 파일 이름만 추출해서 저장
        String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
        uploadedFileNames.add(fileName);

        // then
        System.out.println("Uploaded S3 URL: " + s3Url);
        assertThat(s3Url).isNotNull();
        assertThat(s3Url).contains("https://");
    }

    @Test
    @DisplayName("S3 및 로컬 저장소에 파일 업로드")
    void uploadFileToLocalAndS3() throws IOException {
        // given
        MultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes()
        );
        MultipartFile detailImage = new MockMultipartFile(
                "detail", "detail.jpg", "image/jpeg", "detail data".getBytes()
        );

        Product product = Product.builder().name("Test Product").build();
        productRepository.save(product);
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage))
                .build();

        // when
        productImgService.createProductImgs(product.getNo(), request);


        // ProductImgRepository를 통해 업로드된 S3 URL에서 파일 이름 추출하여 삭제 리스트에 추가
        List<String> s3FileNames = productImgRepository.findAll().stream()
                .map(ProductImg::getImgUrl) // S3 URL이 저장된 필드에 맞게 getImgUrl로 수정 필요
                .map(s3Url -> s3Url.substring(s3Url.lastIndexOf("/") + 1))
                .toList();

        uploadedFileNames.addAll(s3FileNames);

        System.out.println("파일이 S3와 로컬에 업로드되었습니다.");
        System.out.println("업로드된 파일 이름 리스트: " + uploadedFileNames);
    }

    @AfterEach
    void cleanupUploadedFiles() {
        // 업로드된 파일들을 삭제
        for (String fileName : uploadedFileNames) {
            try {
                s3Service.deleteFile(fileName);
                System.out.println("삭제된 파일: " + fileName);
            } catch (Exception e) {
                System.err.println("파일 삭제 실패: " + fileName);
            }
        }
        uploadedFileNames.clear();
    }


}

