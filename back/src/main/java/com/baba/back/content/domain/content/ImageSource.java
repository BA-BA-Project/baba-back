package com.baba.back.content.domain.content;

import com.baba.back.content.exception.ImageSourceBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ImageSource {
    private String imageSource;

    public ImageSource(String imageSource) {
        validateNull(imageSource);
        this.imageSource = imageSource;
    }

    private void validateNull(String imageSource) {
        if (Objects.isNull(imageSource)) {
            throw new ImageSourceBadRequestException("ImageSource는 null일 수 없습니다.");
        }
    }
}
