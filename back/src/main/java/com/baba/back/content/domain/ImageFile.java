package com.baba.back.content.domain;

import com.baba.back.content.exception.ImageFileBadRequestException;
import java.util.Objects;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ImageFile {
    public static final String VALID_START_TYPE = "image";
    public static final String INVALID_END_TYPE = "gif";

    private final MultipartFile file;

    public ImageFile(MultipartFile file) {
        validateType(file);
        this.file = file;
    }

    private void validateType(MultipartFile file) {
        final String contentType = file.getContentType();

        validateTypeNull(contentType);
        validateImage(contentType);
        validateGIF(contentType);
    }

    private void validateTypeNull(String contentType) {
        if (Objects.isNull(contentType)) {
            throw new ImageFileBadRequestException("파일 타입은 null일 수 없습니다.");
        }
    }

    private void validateImage(String contentType) {
        if (!contentType.startsWith(VALID_START_TYPE)) {
            throw new ImageFileBadRequestException("파일이 이미지 형식이 아닙니다.");
        }
    }

    private void validateGIF(String contentType) {
        if (contentType.endsWith(INVALID_END_TYPE)) {
            throw new ImageFileBadRequestException("gif 파일은 올바른 형식이 아닙니다.");
        }
    }
}
