package shoppingmall.ankim.global.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import shoppingmall.ankim.domain.order.dto.OrderTempErrorResponse;
import shoppingmall.ankim.domain.order.exception.OrderTempException;
import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(CustomLogicException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomLogicException e){
        return ApiResponse.toResponseEntity(e.getErrorCode());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ApiResponse.of(e.getBindingResult());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                "필수 요청 필드가 누락되었습니다.",
                List.of(new ApiResponse.FieldError(
                        e.getRequestPartName(),
                        null,
                        "해당 요청 필드는 필수입니다."
                )),
                null
        );
    }

    @ExceptionHandler(OrderTempException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleOrderTempException(OrderTempException e) {
        // OrderTempErrorResponse는 referer 필드를 포함하는 DTO
        OrderTempErrorResponse errorResponse = OrderTempErrorResponse.of(e.getErrorCode().getMessage(), e.getReferer());
        return ApiResponse.of(e.getErrorCode().getHttpStatus(), errorResponse);
    }
}
