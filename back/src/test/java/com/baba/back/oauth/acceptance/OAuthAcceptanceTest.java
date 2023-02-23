package com.baba.back.oauth.acceptance;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.baba.back.AcceptanceTest;
import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class OAuthAcceptanceTest extends AcceptanceTest {

    public static final String BASE_URL = "/api/auth/login";

    @MockBean
    private OAuthClient oAuthClient;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 가입되어_있으면_200을_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("token");
        memberRepository.save(멤버1);

        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .extract();

        final String accessTokenResponse = response.response().jsonPath().get("accessToken");
        final String refreshTokenResponse = response.response().jsonPath().get("refreshToken");

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(accessTokenResponse).isNotBlank(),
                () -> assertThat(refreshTokenResponse).isNotBlank()
        );

    }

    @Test
    void 가입되어_있지_않으면_404를_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("invalidToken");
        given(oAuthClient.getMemberId(any())).willReturn("invalid member");

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .extract();

        // then
        final String accessTokenResponse = response.response().jsonPath().get("accessToken");
        final String refreshTokenResponse = response.response().jsonPath().get("refreshToken");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(accessTokenResponse).isNotBlank(),
                () -> assertThat(refreshTokenResponse).isNotBlank()
        );
    }
}
