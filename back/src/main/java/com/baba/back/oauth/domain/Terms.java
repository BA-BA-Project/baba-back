package com.baba.back.oauth.domain;

import com.baba.back.oauth.exception.TermsBadRequestException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Terms {
    TERMS_1(true, "이용약관 동의",
            "https://sites.google.com/view/baba-agree/%EC%9D%B4%EC%9A%A9%EC%95%BD%EA%B4%80?authuser=1"),
    TERMS_2(true, "개인정보 수집 및 이용 동의",
            "https://sites.google.com/view/baba-agree/%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4%EC%B2%98%EB%A6%AC%EB%B0%A9%EC%B9%A8?authuser=1"),
    TERMS_3(false, "앱 접근권한 허용",
            "https://sites.google.com/view/baba-agree/%EC%95%B1-%EC%A0%91%EA%B7%BC%EA%B6%8C%ED%95%9C-%ED%97%88%EC%9A%A9?authuser=1");

    private final boolean required;
    private final String name;
    private final String url;

    public static boolean isSizeEqualToAllTerms(int size) {
        return Terms.values().length == size;
    }

    public static Terms findByName(String name) {
        return Arrays.stream(Terms.values())
                .filter(terms -> terms.name.equals(name))
                .findAny()
                .orElseThrow(() -> new TermsBadRequestException("{" + name + "}은 존재하지 않는 약관입니다."));
    }
}
