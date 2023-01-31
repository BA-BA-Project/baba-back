package com.baba.back.content.domain;

import com.baba.back.content.domain.content.ImageSource;
import com.baba.back.content.exception.ImageSourceBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class ImageSourceTest {

    @ParameterizedTest
    @NullSource
    void ImageSource는_null일수_없다(String imageSource) {
        Assertions.assertThatThrownBy(() -> new ImageSource(imageSource))
                .isInstanceOf(ImageSourceBadRequestException.class);
    }

    @ParameterizedTest
    @EmptySource
    void ImageSource는_빈값일수_있다(String imageSource) {
        Assertions.assertThatCode(() -> new ImageSource(imageSource))
                .doesNotThrowAnyException();
    }
}