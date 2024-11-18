package shoppingmall.ankim.domain.image.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.exception.DetailImageRequiredException;
import shoppingmall.ankim.domain.image.exception.ImageLimitExceededException;
import shoppingmall.ankim.domain.image.exception.ThumbnailImageRequiredException;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductImgServiceTest {

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImgRepository productImgRepository;

    @MockBean
    private FileService fileService;

    @MockBean
    private S3Service s3Service;

    @Mock
    private MultipartFile thumbnailImage;

    @Mock
    private MultipartFile detailImage;

    @DisplayName("썸네일 이미지와 상세 이미지를 저장할 수 있다.")
    @Test
    void createProductImgs() throws IOException {

        // given
        Product product = createProduct();
        Product save = productRepository.save(product);
        // MockMultipartFile을 통해 썸네일과 상세 이미지 데이터를 생성합니다.
        MultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MultipartFile detailImage = new MockMultipartFile(
                "detail", "detail.jpg", "image/jpeg", "detail data".getBytes());

        // 이를 사용하여 ProductImgServiceRequest 객체를 생성합니다.
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage))
                .build();

        // FileService의 uploadFile 메서드를 Mock 설정하여 호출 시 "test-image.jpg"를 반환하도록 합니다.
        given(fileService.uploadFile(anyString(), anyString(), any(byte[].class)))
                .willReturn("test-image.jpg");

        // when
        // ProductImgService의 createProductImgs 메서드를 호출하여 저장을 테스트합니다.
        productImgService.createProductImgs(save.getNo(),request);

        // then
        // uploadFile 메서드가 2번 호출되었는지 확인하고, 저장된 이미지 개수를 검증합니다.
        verify(fileService, times(2)).uploadFile(anyString(), anyString(), any(byte[].class));
        assertThat(productImgRepository.findAll()).hasSize(2);
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 1장은 필수로 등록해야 한다.")
    @Test
    void createProductImgsRequired() {
        // given
        Product product = createProduct();
        Product save = productRepository.save(product);
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(Collections.emptyList()) // 썸네일 이미지가 없는 경우
                .detailImages(List.of(detailImage))
                .build();

        ProductImgCreateServiceRequest requestWithNoDetail = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(Collections.emptyList()) // 상세 이미지가 없는 경우
                .build();

        // when // then
        assertThatThrownBy(() -> productImgService.createProductImgs(save.getNo(),request))
                .isInstanceOf(ThumbnailImageRequiredException.class)
                .hasMessageContaining("썸네일 이미지는 최소 1개가 필요합니다.");


        assertThatThrownBy(() -> productImgService.createProductImgs(save.getNo(), requestWithNoDetail))
                .isInstanceOf(DetailImageRequiredException.class)
                .hasMessageContaining("상세 이미지는 최소 1개가 필요합니다.");
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 최대 6장까지 등록할 수 있다.")
    @Test
    void createProductImgsLimitExceeded() {
        // given
        Product product = createProduct();
        Product save = productRepository.save(product);
        List<MultipartFile> thumbnails = List.of(thumbnailImage, thumbnailImage, thumbnailImage, thumbnailImage, thumbnailImage, thumbnailImage, thumbnailImage); // 7개 이미지
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(thumbnails)
                .detailImages(List.of(detailImage))
                .build();

        // when // then
        assertThatThrownBy(() -> productImgService.createProductImgs(save.getNo(), request))
                .isInstanceOf(ImageLimitExceededException.class)
                .hasMessageContaining("이미지는 최대 6장까지 업로드할 수 있습니다.");
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 등록한 순서대로 이미지 순서가 정해진다.")
    @Test
    void createProductImgsOrder() throws IOException {
        // given
        Product product = createProduct();
        Product save = productRepository.save(product);
        ProductImgCreateServiceRequest request = ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage, detailImage)) // 2 detail images for ordering test
                .build();

        // FileService mock 설정
        given(fileService.uploadFile(anyString(), anyString(), any(byte[].class)))
                .willReturn("test-image.jpg");

        // when
        productImgService.createProductImgs(save.getNo() , request);

        // then
        List<ProductImg> savedImages = productImgRepository.findAll();
        assertThat(savedImages).hasSize(3);
        assertThat(savedImages.get(0).getOrd()).isEqualTo(1); // Thumbnail order
        assertThat(savedImages.get(1).getOrd()).isEqualTo(1); // Detail order 1
        assertThat(savedImages.get(2).getOrd()).isEqualTo(2); // Detail order 2
    }

    @DisplayName("상품 이미지를 업데이트할 수 있다.")
    @Rollback(value = false)
    @Test
    void updateProductImgs() throws IOException {
        // given
        Product product = createProduct();


        // 기존 이미지 등록
        ProductImg existingThumbnail = ProductImg.builder()
                .imgName("old-thumbnail.jpg")
                .oriImgName("old-thumbnail.jpg")
                .imgUrl("old-thumbnail-url")
                .repimgYn("Y")
                .ord(1)
                .product(product)
                .build();
        product.addProductImg(existingThumbnail);

        ProductImg existingDetail = ProductImg.builder()
                .imgName("old-detail.jpg")
                .oriImgName("old-detail.jpg")
                .imgUrl("old-detail-url")
                .repimgYn("N")
                .ord(1)
                .product(product)
                .build();
        product.addProductImg(existingDetail);

        Product savedProduct = productRepository.save(product);

        // 새로운 이미지 요청 데이터 생성
        MultipartFile newThumbnail = new MockMultipartFile(
                "new-thumbnail", "new-thumbnail.jpg", "image/jpeg", "new thumbnail data".getBytes());
        MultipartFile newDetail1 = new MockMultipartFile(
                "new-detail1", "new-detail1.jpg", "image/jpeg", "new detail data 1".getBytes());
        MultipartFile newDetail2 = new MockMultipartFile(
                "new-detail2", "new-detail2.jpg", "image/jpeg", "new detail data 2".getBytes());

        ProductImgUpdateServiceRequest request = ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(List.of(newThumbnail))
                .detailImages(List.of(newDetail1, newDetail2))
                .build();

        // FileService 및 S3Service Mock 설정
        given(fileService.uploadFile(anyString(), anyString(), any(byte[].class)))
                .willReturn("new-test-image.jpg");
        given(s3Service.uploadSingle(any(MultipartFile.class)))
                .willReturn("new-s3-url");

        // when
        productImgService.updateProductImgs(savedProduct.getNo(), request);

        // then
        List<ProductImg> updatedImages = productImgRepository.findAll();

        // 기존 이미지는 삭제되었는지 검증
        assertThat(updatedImages).hasSize(3); // 1 Thumbnail + 2 Details
        verify(fileService, times(2)).deleteFile(anyString());
        verify(s3Service, times(2)).deleteFile(anyString());

        // 새로운 이미지 검증
        ProductImg newThumbnailImg = updatedImages.stream()
                .filter(img -> img.getRepimgYn().equals("Y"))
                .findFirst()
                .orElseThrow();
        assertThat(newThumbnailImg.getOriImgName()).isEqualTo("new-thumbnail.jpg");

        ProductImg newDetailImg1 = updatedImages.stream()
                .filter(img -> img.getRepimgYn().equals("N") && img.getOrd() == 1)
                .findFirst()
                .orElseThrow();
        assertThat(newDetailImg1.getOriImgName()).isEqualTo("new-detail1.jpg");

        ProductImg newDetailImg2 = updatedImages.stream()
                .filter(img -> img.getRepimgYn().equals("N") && img.getOrd() == 2)
                .findFirst()
                .orElseThrow();
        assertThat(newDetailImg2.getOriImgName()).isEqualTo("new-detail2.jpg");
    }


    private Product createProduct() {
        return Product.builder()
                .name("Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
    }
}
