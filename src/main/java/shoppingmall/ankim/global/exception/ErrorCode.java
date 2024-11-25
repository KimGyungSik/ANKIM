package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    CATEGORY_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "카테고리 이름은 50자 이하로 입력해야 합니다."),
    DUPLICATE_MIDDLE_CATEGORY_NAME(HttpStatus.CONFLICT, "중복된 중분류 이름이 존재합니다."),
    DUPLICATE_SUB_CATEGORY_NAME(HttpStatus.CONFLICT, "해당 중분류 아래에 중복된 소분류 이름이 존재합니다."),
    CHILD_CATEGORY_EXISTS(HttpStatus.CONFLICT, "삭제할 중분류에 소분류가 존재하므로 삭제할 수 없습니다."),
    CATEGORY_LINKED_WITH_PRODUCT(HttpStatus.BAD_REQUEST, "해당 카테고리에 속한 상품이 존재하므로 삭제할 수 없습니다."),


    // 상품 이미지
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
    FILE_UPLOAD_FAIL(HttpStatus.NOT_FOUND, "파일 업로드에 실패하였습니다."),
    THUMBNAIL_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "썸네일 이미지는 최소 1개가 필요합니다."),
    DETAIL_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "상세 이미지는 최소 1개가 필요합니다."),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지는 최대 6장까지 업로드할 수 있습니다."),

    // S3 파일 처리
    S3_FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3에 파일 업로드 중 오류가 발생했습니다."),
    S3_FILE_DELETION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3에서 파일 삭제 중 오류가 발생했습니다."),
    S3_INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 형식입니다. S3로 업로드할 수 없습니다."),

    // 옵션
    DUPLICATE_OPTION_VALUE(HttpStatus.BAD_REQUEST, "옵션 값이 중복되었습니다"),
    DUPLICATE_OPTION_GROUP(HttpStatus.BAD_REQUEST, "옵션 항목이 중복되었습니다"),
    OPTION_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND,"옵션 항목을 찾을 수 없습니다." ),
    OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND,"옵션 값을 찾을 수 없습니다." ),
    INSUFFICIENT_OPTION_VALUES(HttpStatus.BAD_REQUEST, "옵션 그룹에는 최소 하나 이상의 옵션 값이 필요합니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,"상품을 찾을 수 없습니다." ),
    PRODUCT_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "상품명은 60자 이하로 입력해야 합니다."),
    CANNOT_MODIFY_SELLING_PRODUCT(HttpStatus.BAD_REQUEST, "판매 중인 상품은 수정할 수 없습니다.");



    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}