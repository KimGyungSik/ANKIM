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
    CANNOT_MODIFY_SELLING_PRODUCT(HttpStatus.BAD_REQUEST, "판매 중인 상품은 수정할 수 없습니다."),

    // 품목
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "품목을 찾을 수 없습니다."),

    // 주문 품목
    ORDER_ITEM_QTY_INVALID(HttpStatus.BAD_REQUEST, "주문 수량은 1개 이상이어야 합니다."),
    DISCOUNT_PRICE_INVALID(HttpStatus.BAD_REQUEST, "상품 원가 금액이 할인 적용된 상품 금액보다 작을 수 없습니다."),

    // 회원 주소
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 배송지를 찾을 수 없습니다."),
    DEFAULT_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 기본 배송지를 찾을 수 없습니다."),

    // 약관 관련 에러 코드
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 약관에 동의하지 않았습니다."),

    // 인증 관련 에러 코드
    ADMIN_ID_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    INVALID_LOGIN_ID(HttpStatus.BAD_REQUEST, "아이디가 검증되지 않았습니다."),
    MEMBER_ID_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."), // 메일 전송에 실패했습니다.
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    MISSING_REQUIRED_ID(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),

    // 회원정보 관련 에러 코드
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_STATUS_LOCKED(HttpStatus.FORBIDDEN, "로그인 시도 가능 횟수를 초과했습니다. 10분 동안 로그인 시도가 불가능합니다."),
    INVALID_MEMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자입니다."),

    // Token 관련 에러 코드
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Access Token이 요청에 포함되지 않았습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh Token이 요청에 포함되지 않았습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    TOKEN_REISSUE_FAILED(HttpStatus.BAD_REQUEST, "Token 재발급 중 오류 발생"),
    TOKEN_VALIDATION_ERROR(HttpStatus.BAD_REQUEST,"유효하지 않은 토큰 입니다."),

    // 쿠키 관련 에러 코드
    COOKIE_NOT_INCLUDED(HttpStatus.BAD_REQUEST, "쿠키가 요청에 포함되어 있지 않습니다."),

    // Redis 관련 에러 코드
    REDIS_ID_VALIDATION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    ;

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}