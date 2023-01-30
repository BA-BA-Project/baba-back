package com.baba.back.content.domain;

import com.baba.back.content.exception.ImageFileBadRequestException;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class ImageFile {
    private final MultipartFile file;

    public ImageFile(MultipartFile file) {
        validateImage(file);
        this.file = file;
    }

    private void validateImage(MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "image/png")) {
            throw new ImageFileBadRequestException("파일이 이미지 형식이 아닙니다.");
        }
    }
}
