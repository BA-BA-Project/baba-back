package com.baba.back.oauth.acceptance;

import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.AcceptanceTest;
import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.RefreshTokenProvider;
import com.baba.back.oauth.service.SignTokenProvider;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;

public class OAuthAcceptanceTest extends AcceptanceTest {

    public static final String BASE_URL = "/api/auth";
    private static final String MEMBER_BASE_PATH = "/api/members/baby";
    private static final String MEMBER_ID = "memberId";

    @MockBean
    private OAuthClient oAuthClient;

    @Autowired
    private SignTokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @SpyBean
    private RefreshTokenProvider refreshTokenProvider;

    @Test
    void 가입되어_있으면_access_token과_refresh_token_과_200을_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("token");
        memberRepository.save(멤버1);

        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = post(BASE_URL + "/login", request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, LoginTokenResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(toObject(response, LoginTokenResponse.class).refreshToken()).isNotBlank()
        );

    }

    @Test
    void 가입되어_있지_않으면_sign_token_과_404를_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("invalidToken");
        given(oAuthClient.getMemberId(any())).willReturn("invalid member");

        // when
        final ExtractableResponse<Response> response = post(BASE_URL + "/login", request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(toObject(response, SignTokenResponse.class).signToken()).isNotNull()
        );
    }

    @Test
    void 토큰재발급시_refresh토큰의_만료기간이_하루보다_많이_남았으면_access토큰을_재발급하고_201을_응답한다() {
        // given
        final String token = tokenProvider.createToken(MEMBER_ID);
        final ExtractableResponse<Response> signUpResponse = post(MEMBER_BASE_PATH,
                Map.of("Authorization", "Bearer " + token), 멤버_가입_요청);

        final String refreshToken = toObject(signUpResponse, MemberSignUpResponse.class).refreshToken();
        given(refreshTokenProvider.checkExpiration(refreshToken)).willReturn(false);

        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when
        final ExtractableResponse<Response> response = post(BASE_URL + "/refresh", request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).refreshToken()).isEqualTo(refreshToken)
        );

        then(refreshTokenProvider).should(times(1)).createToken(any());
    }

    @Test
    void 토큰재발급시_refresh토큰의_만료기간이_하루_이하로_남았으면_access토큰과_refresh토큰을_재발급하고_201을_응답한다() {
        // given
        final String token = tokenProvider.createToken(MEMBER_ID);
        final ExtractableResponse<Response> signUpResponse = post(MEMBER_BASE_PATH,
                Map.of("Authorization", "Bearer " + token), 멤버_가입_요청);

        final String refreshToken = toObject(signUpResponse, MemberSignUpResponse.class).refreshToken();
        given(refreshTokenProvider.checkExpiration(refreshToken)).willReturn(true);

        final TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when
        final ExtractableResponse<Response> response = post(BASE_URL + "/refresh", request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).accessToken()).isNotBlank()
        );

        then(refreshTokenProvider).should(times(2)).createToken(any());
    }


}
