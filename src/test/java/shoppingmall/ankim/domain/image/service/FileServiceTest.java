package shoppingmall.ankim.domain.image.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.FileSystemUtils;
import shoppingmall.ankim.domain.image.exception.FileNotFoundException;
import shoppingmall.ankim.global.config.uuid.UuidHolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class FileServiceTest {
    @Autowired
    FileService fileService;

    @MockBean
    UuidHolder uuidHolder;

    private final String uploadPath = "test-upload";

    @BeforeEach
    void setUp() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @AfterEach
    void tearDown() {
        File uploadDir = new File(uploadPath);
        if (uploadDir.exists()) {
            // 업로드 디렉토리 삭제
            for (File file : Objects.requireNonNull(uploadDir.listFiles())) {
                file.delete();
            }
            uploadDir.delete();
        }
    }

    @DisplayName("파일을 업로드할 수 있다.")
    @Test
    void uploadFile() throws Exception {
        // given
        String originalFileName = "test.jpg";
        byte[] fileData = "sample data".getBytes();

        given(uuidHolder.random()).willReturn("fixed-uuid");

        // when
        String savedFileName = fileService.uploadFile(uploadPath, originalFileName, fileData);

        // then
        assertThat(savedFileName).isEqualTo("fixed-uuid.jpg");
        assertThat(Files.exists(Paths.get(uploadPath, savedFileName))).isTrue();

        // Clean up
        FileSystemUtils.deleteRecursively(new File(uploadPath));
    }

    @DisplayName("존재하지 않는 파일을 삭제 시도할 때 예외가 발생한다.")
    @Test
    void deleteNonExistentFile() {
        // given
        String nonExistentFilePath = "test-upload/nonexistent-file.jpg";

        // when // then
        assertThrows(FileNotFoundException.class, () -> fileService.deleteFile(nonExistentFilePath));
    }

    @DisplayName("파일을 삭제할 수 있다.")
    @Test
    void deleteFile() throws Exception {
        // given
        String originalFileName = "test.jpg";
        byte[] fileData = "sample data".getBytes();
        given(uuidHolder.random()).willReturn("fixed-uuid");
        String savedFileName = fileService.uploadFile(uploadPath, originalFileName, fileData);

        // when
        fileService.deleteFile(uploadPath + "/" + savedFileName);

        // then
        assertThat(Files.exists(Paths.get(uploadPath, savedFileName))).isFalse();
    }
}
