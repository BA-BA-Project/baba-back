package com.baba.back.oauth.acceptance;

import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.AcceptanceTest;
import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.service.RefreshTokenProvider;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;

class OAuthAcceptanceTest extends AcceptanceTest {

    @MockBean
    private OAuthClient oAuthClient;

    @SpyBean
    private RefreshTokenProvider refreshTokenProvider;

    @Test
    void 소셜_로그인_요청_시_이미_가입되어_있으면_access_token과_refresh_token_과_200을_응답한다() {
        // given
        아기_등록_회원가입_요청_멤버_1();
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = 소셜_로그인_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, SocialLoginResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(toObject(response, SocialLoginResponse.class).refreshToken()).isNotBlank()
        );

    }

    @Test
    void 소셜_로그인_요청_시_가입되어_있지_않으면_404를_응답한다() {
        // given
        given(oAuthClient.getMemberId(any())).willReturn("not signed up member");

        // when
        final ExtractableResponse<Response> response = 소셜_로그인_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 토큰_재발급_요청_시_refresh토큰의_만료기간이_하루보다_많이_남았으면_access토큰을_재발급하고_201을_응답한다() {
        // given
        final ExtractableResponse<Response> 회원가입_응답 = 아기_등록_회원가입_요청_멤버_1();
        final String refreshToken = toObject(회원가입_응답, MemberSignUpResponse.class).refreshToken();
        given(refreshTokenProvider.isExpiringSoon(refreshToken)).willReturn(false);

        // when
        final ExtractableResponse<Response> response = 토큰_재발급_요청(new TokenRefreshRequest(refreshToken));

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).refreshToken()).isEqualTo(refreshToken)
        );

        then(refreshTokenProvider).should(times(1)).createToken(any());
    }

    @Test
    void 토큰_재발급_요청_시_refresh토큰의_만료기간이_하루_이하로_남았으면_access토큰과_refresh토큰을_재발급하고_201을_응답한다() {
        // given
        final ExtractableResponse<Response> 회원가입_응답 = 아기_등록_회원가입_요청_멤버_1();
        final String refreshToken = toObject(회원가입_응답, MemberSignUpResponse.class).refreshToken();
        given(refreshTokenProvider.isExpiringSoon(refreshToken)).willReturn(true);

        // when
        final ExtractableResponse<Response> response = 토큰_재발급_요청(new TokenRefreshRequest(refreshToken));

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, TokenRefreshResponse.class).accessToken()).isNotBlank()
        );

        then(refreshTokenProvider).should(times(2)).createToken(any());
    }
}
