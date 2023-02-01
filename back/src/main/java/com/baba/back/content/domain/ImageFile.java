package com.baba.back.content.domain;

import com.baba.back.content.exception.ImageFileBadRequestException;
import java.util.Objects;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ImageFile {
    private final MultipartFile file;

    public ImageFile(MultipartFile file) {
        validateType(file);
        this.file = file;
    }

    private void validateType(MultipartFile file) {
        final String contentType = file.getContentType();

        if (Objects.isNull(contentType)) {
            throw new ImageFileBadRequestException("파일 타입은 null일 수 없습니다.");
        }
        if (!contentType.startsWith("image")) {
            throw new ImageFileBadRequestException("파일이 이미지 형식이 아닙니다.");
        }
        if (contentType.endsWith("gif")) {
            throw new ImageFileBadRequestException("gif 파일은 올바른 형식이 아닙니다.");
        }
    }
}
