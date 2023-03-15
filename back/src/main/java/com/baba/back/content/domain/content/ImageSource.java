package com.baba.back.content.domain.content;

import com.baba.back.content.exception.ImageSourceBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ImageSource {

    @Column(name = "image_source")
    private String value;

    public ImageSource(String value) {
        validateNull(value);
        this.value = value;
    }

    private void validateNull(String imageSource) {
        if (Objects.isNull(imageSource)) {
            throw new ImageSourceBadRequestException("ImageSource는 null일 수 없습니다.");
        }
    }
}
