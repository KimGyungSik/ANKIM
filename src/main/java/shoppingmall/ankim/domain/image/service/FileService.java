package shoppingmall.ankim.domain.image.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.image.exception.FileNotFoundException;
import shoppingmall.ankim.domain.image.exception.FileUploadException;
import shoppingmall.ankim.global.config.uuid.UuidHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import static shoppingmall.ankim.global.exception.ErrorCode.FILE_NOT_FOUND;
import static shoppingmall.ankim.global.exception.ErrorCode.FILE_UPLOAD_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UuidHolder uuidHolder;

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) {
        String uuid = uuidHolder.random();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(fileData);
        } catch (Exception e) {
            log.error("파일 업로드 중 오류가 발생했습니다: {}", e.getMessage());
            throw new FileUploadException(FILE_UPLOAD_FAIL);
        }

        return savedFileName;
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }
    }

}
