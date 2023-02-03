package com.baba.back.content.domain;

public interface FileHandler {
    /**
     * 이미지를 외부에 저장한다.
     *
     * @return 외부에 저장된 이미지의 URL
     */
    String upload(ImageFile imageFile);
}
