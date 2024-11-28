package shoppingmall.ankim.domain.image.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.global.config.uuid.UuidHolder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@DisabledIfSystemProperty(named = "os.name", matches = "Mac OS X")
@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class FileServiceRealTest {

    @MockBean
    S3Service s3Service;

    @Autowired
    FileService fileService;

    @MockBean
    UuidHolder uuidHolder;

    @Value("${itemImgLocation}")
    private String uploadPath;

    @Test
    @DisplayName("파일이 실제로 지정된 경로에 업로드되는지 확인한다.")
    void uploadFileTest() {
        // given
        given(uuidHolder.random()).willReturn("test-uuid");
        String originalFileName = "test-image.jpg";
        byte[] fileData = "sample data".getBytes();

        // when
        String savedFileName = fileService.uploadFile(uploadPath, originalFileName, fileData);

        // then
        File uploadedFile = new File(uploadPath + "/" + savedFileName);
        assertTrue(uploadedFile.exists(), "업로드된 파일이 경로에 존재해야 합니다.");

//         업로드된 파일 삭제 (테스트 후 깨끗한 환경 유지)
        uploadedFile.delete();
    }
}
