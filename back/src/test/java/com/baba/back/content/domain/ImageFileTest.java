package com.baba.back.content.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.content.exception.ImageFileBadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageFileTest {

    @Test
    void 이미지가_아닌_파일을_받으면_예외를_던진다() {
        // given
        MultipartFile mockFile = new MockMultipartFile("photo", "file.png", "text/plain",
                "Spring Framework".getBytes());

        // when & then
        assertThatThrownBy(() -> new ImageFile(mockFile))
                .isInstanceOf(ImageFileBadRequestException.class);
    }

    @Test
    void 이미지_파일을_받으면_객체를_생성한다() {
        // given
        MultipartFile mockFile = new MockMultipartFile("photo", "file.png", "image/png", "Spring Framework".getBytes());

        // when & then
        assertThatCode(() -> new ImageFile(mockFile))
                .doesNotThrowAnyException();
    }
}