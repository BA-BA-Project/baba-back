package com.baba.back.content.domain;

import com.baba.back.common.S3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ImageSaverS3 implements ImageSaver {

    private final S3Handler s3Handler;

    @Override
    public String save(ImageFile imageFile) {
        return s3Handler.upload(imageFile.getFile());
    }
}
