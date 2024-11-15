package shoppingmall.ankim.domain.image.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.ankim.domain.image.exception.S3FileDeletionException;
import shoppingmall.ankim.domain.image.exception.S3FileUploadException;
import shoppingmall.ankim.domain.image.exception.S3InvalidFileFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public String uploadSingle(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new S3FileUploadException(S3_FILE_UPLOAD_ERROR);
        }

        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return s3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new S3FileUploadException(S3_FILE_UPLOAD_ERROR);
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new S3FileUploadException(S3_FILE_UPLOAD_ERROR);
        }
        List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG");
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (!allowedExtensions.contains(fileExtension)) {
            throw new S3InvalidFileFormatException(S3_INVALID_FILE_FORMAT);
        }
        return fileExtension;
    }

    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new S3FileDeletionException(S3_FILE_DELETION_ERROR);
        }
    }
}
