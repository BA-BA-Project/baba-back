package com.baba.back.common;

import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.exception.AuthenticationException;
import com.baba.back.exception.AuthorizationException;
import com.baba.back.exception.BadRequestException;
import com.baba.back.exception.NotFoundException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionResponse("인증에 실패했습니다."));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionResponse("권한이 없습니다."));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse("요청한 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        final String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn(message);

        return ResponseEntity.badRequest().body(new ExceptionResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse("처리할 수 없는 예외입니다."));
    }
}