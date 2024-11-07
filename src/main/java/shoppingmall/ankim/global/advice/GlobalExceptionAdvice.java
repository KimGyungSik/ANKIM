package shoppingmall.ankim.global.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.response.ApiResponse;


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
}
