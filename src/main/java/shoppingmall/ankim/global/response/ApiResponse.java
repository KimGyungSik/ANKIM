package shoppingmall.ankim.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ApiResponse<T> {

    private final int code;
    private final HttpStatus status;
    private final String message;
    private final List<FieldError> fieldErrors;
    private final T data;
    private final boolean isJwtError;

    @Builder
    public ApiResponse(HttpStatus status, String message, List<FieldError> fieldErrors, T data, boolean isJwtError) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.data = data;
        this.isJwtError = isJwtError;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus,String message, List<FieldError> fieldErrors, T data) {
        return new ApiResponse<>(httpStatus, message,fieldErrors,data, false);
    }
    public static <T> ApiResponse<T> of(BindingResult bindingResult) {
        List<FieldError> fieldErrors = FieldError.of(bindingResult);
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors, null, false);
    }
    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return of(httpStatus, httpStatus.name(),null, data);
    }
    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(HttpStatus.OK, "OK", null, null, false);
    }

    // ErrorCode를 받아 ApiResponse 생성
    public static <T> ApiResponse<T> of(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getHttpStatus(), errorCode.getMessage(), null, null, errorCode.isJwtError());
    }

    public static <T> ApiResponse<T> ok(HttpStatus httpStatus, String message) {
        return new ApiResponse<>(httpStatus, message, null, null, false);
    }

    @Getter
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String reason;

        public FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ?
                                    "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    public static ResponseEntity<ApiResponse<Object>> toResponseEntity(ErrorCode errorCode){
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .status(errorCode.getHttpStatus())
                        .message(errorCode.getMessage())
                        .data(null)
                        .fieldErrors(null)
                        .isJwtError(errorCode.isJwtError())
                        .build());
    }
}

