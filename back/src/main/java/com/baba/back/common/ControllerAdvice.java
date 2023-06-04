package com.baba.back.common;

import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.exception.AuthenticationException;
import com.baba.back.exception.AuthorizationException;
import com.baba.back.exception.BadRequestException;
import com.baba.back.exception.NotFoundException;
import com.baba.back.exception.ServerException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    private static final String BAD_REQUEST_ERROR_MESSAGE = "잘못된 요청입니다.";
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST_ERROR_MESSAGE));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleBindingExceptions(BindException exception) {
        final String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        logger.warn(message);

        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST_ERROR_MESSAGE));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST_ERROR_MESSAGE));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionResponse("인증에 실패했습니다."));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionResponse("권한이 없습니다."));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse("요청한 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleServerException(ServerException exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse("처리할 수 없는 예외입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        logger.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse("처리할 수 없는 예외입니다."));
    }
}
