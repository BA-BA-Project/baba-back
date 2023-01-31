package com.baba.back.content.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class ImageSaverS3Test {

    @Autowired
    private ImageSaver imageSaver;

    @Test
    void 이미지_업로드에_성공한다() {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile("photo", "test_file.png", "image/png",
                "Spring Framework".getBytes());
        ImageFile imageFile = new ImageFile(mockFile);

        // when & then
        Assertions.assertThatCode(() -> imageSaver.save(imageFile))
                .doesNotThrowAnyException();
    }
}