package com.baba.back.content.domain;

import com.baba.back.content.domain.content.ImageSource;
import com.baba.back.content.exception.ImageSourceBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageSourceTest {

    @Test
    void ImageSource는_null일수_없다() {
        final String imageSource = null;
        Assertions.assertThatThrownBy(() -> new ImageSource(imageSource))
                .isInstanceOf(ImageSourceBadRequestException.class);
    }

    @Test
    void ImageSource는_빈값일수_있다() {
        final String imageSource = "";
        Assertions.assertThatCode(() -> new ImageSource(imageSource))
                .doesNotThrowAnyException();
    }
}