package com.baba.back.content.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.content.exception.ImageFileBadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageFileTest {

    @ParameterizedTest
    @ValueSource(strings = {"text/plain", "text/html", "application/octet-stream", "image/gif"})
    void 이미지가_아닌_파일을_받으면_예외를_던진다(String contentType) {
        // given
        MultipartFile mockFile = new MockMultipartFile("photo", "file.png", contentType,
                "Spring Framework".getBytes());

        // when & then
        assertThatThrownBy(() -> new ImageFile(mockFile))
                .isInstanceOf(ImageFileBadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/bmp", "image/jpeg"})
    void 이미지_파일을_받으면_객체를_생성한다(String contentType) {
        // given
        MultipartFile mockFile = new MockMultipartFile("photo", "file.png", contentType, "Spring Framework".getBytes());

        // when & then
        assertThatCode(() -> new ImageFile(mockFile))
                .doesNotThrowAnyException();
    }
}