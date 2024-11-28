package shoppingmall.ankim.domain.image.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisabledIfSystemProperty(named = "os.name", matches = "Mac OS X")@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ProductImgServiceS3IntegrationTest {

    @MockBean
    private InitProduct initProduct;

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private ProductImgRepository productImgRepository;

    @Autowired
    private ProductRepository productRepository;

    private final List<String> uploadedImgUrls = new ArrayList<>();
    private final List<String> uploadedLocalFilePaths = new ArrayList<>();

    @AfterEach
    void cleanupUploadedImgUrl() {
        // 업로드된 S3 파일 삭제
        for (String imgUrl : uploadedImgUrls) {
            try {
                s3Service.deleteFile(imgUrl);
                System.out.println("삭제된 S3 파일: " + imgUrl);
            } catch (Exception e) {
                System.err.println("S3 파일 삭제 실패: " + imgUrl);
            }
        }
        uploadedImgUrls.clear();

        // 업로드된 로컬 파일 삭제
        for (String filePath : uploadedLocalFilePaths) {
            try {
                fileService.deleteFile(filePath);
                System.out.println("삭제된 로컬 파일: " + filePath);
            } catch (Exception e) {
                System.err.println("로컬 파일 삭제 실패: " + filePath);
            }
        }
        uploadedLocalFilePaths.clear();
    }

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
        uploadedImgUrls.add(fileName);

        // then
        System.out.println("Uploaded S3 URL: " + s3Url);
        assertThat(s3Url).isNotNull();
        assertThat(s3Url).contains("https://");
    }

    @Test
    @DisplayName("상품 이미지를 S3 및 로컬 저장소에 파일 업로드를 할 수 있다.")
    void uploadFileToLocalAndS3() throws IOException {
        // given
        MultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes()
        );
        MultipartFile detailImage = new MockMultipartFile(
                "detail", "detail.jpg", "image/jpeg", "detail data".getBytes()
        );

        Product product = Product.builder().name("Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
        productRepository.save(product);
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage))
                .build();

        // when
        productImgService.createProductImgs(product.getNo(), request);

        // ProductImgRepository를 통해 업로드된 S3 URL에서 파일 이름 추출하여 삭제 리스트에 추가
        List<String> s3FileNames = productImgRepository.findAll().stream()
                .map(ProductImg::getImgUrl)
                .map(s3Url -> s3Url.substring(s3Url.lastIndexOf("/") + 1))
                .toList();
        uploadedImgUrls.addAll(s3FileNames);

        List<String> localFilePaths = productImgRepository.findAll().stream()
                .map(ProductImg::getImgName)
                .map(fileName -> itemImgLocation + "/" + fileName)
                .toList();
        uploadedLocalFilePaths.addAll(localFilePaths);

        System.out.println("파일이 S3와 로컬에 업로드되었습니다.");
        System.out.println("업로드된 로컬 파일 경로 리스트: " + uploadedLocalFilePaths);
    }

    @Test
    @DisplayName("상품 이미지를 S3 및 로컬 파일 시스템에 업데이트할 수 있다.")
    void updateProductImgsIntegrationTest() throws IOException {
        // given
        Product product = Product.builder().name("Integration Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
        Product savedProduct = productRepository.save(product);

        // 기존 이미지 요청 데이터 생성
        MultipartFile oldThumbnail = new MockMultipartFile(
                "old-thumbnail", "old-thumbnail.jpg", "image/jpeg", "old thumbnail data".getBytes());
        MultipartFile oldDetail = new MockMultipartFile(
                "old-detail", "old-detail.jpg", "image/jpeg", "old detail data".getBytes());

        ProductImgCreateServiceRequest initialRequest = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(oldThumbnail))
                .detailImages(List.of(oldDetail))
                .build();

        // 기존 이미지를 등록 (createProductImgs 호출)
        productImgService.createProductImgs(savedProduct.getNo(), initialRequest);

        // 새로운 이미지 요청 데이터 생성
        MultipartFile newThumbnail = new MockMultipartFile(
                "new-thumbnail", "new-thumbnail.jpg", "image/jpeg", "new thumbnail data".getBytes());
        MultipartFile newDetail1 = new MockMultipartFile(
                "new-detail1", "new-detail1.jpg", "image/jpeg", "new detail data 1".getBytes());
        MultipartFile newDetail2 = new MockMultipartFile(
                "new-detail2", "new-detail2.jpg", "image/jpeg", "new detail data 2".getBytes());

        ProductImgUpdateServiceRequest updateRequest = ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(List.of(newThumbnail))
                .detailImages(List.of(newDetail1, newDetail2))
                .build();

        // when
        productImgService.updateProductImgs(savedProduct.getNo(), updateRequest);

        // then
        List<ProductImg> updatedImages = productImgRepository.findAll();

        // 기존 이미지는 삭제되었는지 검증
        assertThat(updatedImages).hasSize(3); // 1 Thumbnail + 2 Details
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("new-thumbnail.jpg"));
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("new-detail1.jpg"));
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("new-detail2.jpg"));

        // 업로드된 파일 이름 기록
        List<String> uploadedS3FileNames = updatedImages.stream()
                .map(ProductImg::getImgUrl)
                .map(url -> url.substring(url.lastIndexOf("/") + 1))
                .toList();
        uploadedImgUrls.addAll(uploadedS3FileNames);

        List<String> uploadedFilePaths = updatedImages.stream()
                .map(ProductImg::getImgName)
                .map(fileName -> itemImgLocation + "/" + fileName)
                .toList();
        uploadedLocalFilePaths.addAll(uploadedFilePaths);

        System.out.println("업로드된 로컬 파일 경로 리스트: " + uploadedLocalFilePaths);
    }

    @Test
    @DisplayName("요청에 포함된 기존 이미지는 로컬과 S3에 유지된다.")
    void updateProductImgsKeepsExistingImages() throws IOException {
        // given
        Product product = Product.builder().name("Integration Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
        Product savedProduct = productRepository.save(product);

        MultipartFile thumbnail = createMockFile("existing-thumbnail.jpg", "image/jpeg", "thumbnail data");
        MultipartFile detail = createMockFile("existing-detail.jpg", "image/jpeg", "detail data");

        productImgService.createProductImgs(product.getNo(), ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnail))
                .detailImages(List.of(detail))
                .build());

        ProductImgUpdateServiceRequest updateRequest = ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnail)) // 기존 썸네일 유지
                .detailImages(List.of(detail))     // 기존 상세 이미지 유지
                .build();

        // when
        productImgService.updateProductImgs(product.getNo(), updateRequest);

        // then
        List<ProductImg> updatedImages = productImgRepository.findAll();

        assertThat(updatedImages).hasSize(2);
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("existing-thumbnail.jpg"));
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("existing-detail.jpg"));

        // 업로드된 파일 이름 기록
        List<String> uploadedS3FileNames = updatedImages.stream()
                .map(ProductImg::getImgUrl)
                .map(url -> url.substring(url.lastIndexOf("/") + 1))
                .toList();
        uploadedImgUrls.addAll(uploadedS3FileNames);

        List<String> uploadedFilePaths = updatedImages.stream()
                .map(ProductImg::getImgName)
                .map(fileName -> itemImgLocation + "/" + fileName)
                .toList();
        uploadedLocalFilePaths.addAll(uploadedFilePaths);
    }

    @Test
    @DisplayName("요청에 없는 기존 이미지는 로컬 및 S3에서도 삭제된다.")
    void updateProductImgsDeletesRemovedImages() throws IOException {
        // given
        Product product = Product.builder().name("Integration Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
        Product savedProduct = productRepository.save(product);

        MultipartFile thumbnail = createMockFile("old-thumbnail.jpg", "image/jpeg", "old thumbnail data");
        MultipartFile detail = createMockFile("old-detail.jpg", "image/jpeg", "old detail data");

        productImgService.createProductImgs(product.getNo(), ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnail))
                .detailImages(List.of(detail))
                .build());

        ProductImgUpdateServiceRequest updateRequest = ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(Collections.emptyList()) // 기존 썸네일 삭제
                .detailImages(Collections.emptyList())    // 기존 상세 이미지 삭제
                .build();

        // when
        productImgService.updateProductImgs(product.getNo(), updateRequest);

        // then
        List<ProductImg> updatedImages = productImgRepository.findAll();
        assertThat(updatedImages).isEmpty();
    }

    @Test
    @DisplayName("요청에 포함된 새로운 이미지는 로컬 및 S3에도 성공적으로 추가한다.")
    void updateProductImgsAddsNewImages() throws IOException {
        // given
        Product product = Product.builder().name("Integration Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
        Product savedProduct = productRepository.save(product);

        MultipartFile newThumbnail = createMockFile("new-thumbnail.jpg", "image/jpeg", "new thumbnail data");
        MultipartFile newDetail = createMockFile("new-detail.jpg", "image/jpeg", "new detail data");

        ProductImgUpdateServiceRequest updateRequest = ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(List.of(newThumbnail))
                .detailImages(List.of(newDetail))
                .build();

        // when
        productImgService.updateProductImgs(product.getNo(), updateRequest);

        // then
        List<ProductImg> updatedImages = productImgRepository.findAll();
        assertThat(updatedImages).hasSize(2);
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("new-thumbnail.jpg"));
        assertThat(updatedImages).anyMatch(img -> img.getOriImgName().equals("new-detail.jpg"));

        // 업로드된 파일 이름 기록
        List<String> uploadedS3FileNames = updatedImages.stream()
                .map(ProductImg::getImgUrl)
                .map(url -> url.substring(url.lastIndexOf("/") + 1))
                .toList();
        uploadedImgUrls.addAll(uploadedS3FileNames);

        List<String> uploadedFilePaths = updatedImages.stream()
                .map(ProductImg::getImgName)
                .map(fileName -> itemImgLocation + "/" + fileName)
                .toList();
        uploadedLocalFilePaths.addAll(uploadedFilePaths);
    }

    private MultipartFile createMockFile(String fileName, String contentType, String content) {
        return new MockMultipartFile(fileName, fileName, contentType, content.getBytes());
    }
}
