package com.baba.back.oauth.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class AuthorizationExtractor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESS_TOKEN_TYPE = AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";
    public static final String BEARER_TYPE = "Bearer";

    private AuthorizationExtractor() {
        throw new IllegalStateException(AuthorizationExtractor.class.getSimpleName() + " 기본 생성자 사용 불가");
    }

    /**
     * Input : Authorization: Bearer <토큰값> Output : <토큰값>
     */
    public static String extractOrThrow(final HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
                String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
                request.setAttribute(ACCESS_TOKEN_TYPE, value.substring(0, BEARER_TYPE.length()).trim());
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        throw new IllegalArgumentException("요청에서 토큰을 추출할 수 없습니다.");
    }
}
