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

public class OAuthAcceptanceTest extends AcceptanceTest {

    public static final String BASE_URL = "/api/auth/login";

    @MockBean
    private OAuthClient oAuthClient;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 가입되어_있으면_멤버_토큰을_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("token");
        memberRepository.save(멤버1);

        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .extract();

        final String accessToken = response.response().jsonPath().get("accessToken");

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(accessToken).isNotNull()
        );

    }

    @Test
    void 가입되어_있지_않으면_회원가입_토큰을_응답한다() {
        // given
        final SocialTokenRequest request = new SocialTokenRequest("invalidToken");
        given(oAuthClient.getMemberId(any())).willReturn("invalid member");

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .extract();

        // then
        final String signTokenResponse = response.response().jsonPath().get("accessToken");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(signTokenResponse).isNotNull()
        );
    }
}
