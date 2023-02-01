package com.baba.back.swagger;

import com.baba.back.common.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
public @interface UnAuthorizedResponse {
}
