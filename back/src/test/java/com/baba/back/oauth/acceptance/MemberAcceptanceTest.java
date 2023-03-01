package com.baba.back.oauth.acceptance;

import static com.baba.back.SimpleRestAssured.get;
import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.SimpleRestAssured;
import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.IconColor;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.service.AccessTokenProvider;
import com.baba.back.oauth.service.SignTokenProvider;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class MemberAcceptanceTest extends AcceptanceTest {

    private static final String MEMBER_BASE_PATH = "/api/members";
    private static final String MEMBER_ID = "memberId";

    @Autowired
    private SignTokenProvider signTokenProvider;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Test
    void 요청에_sign_토큰이_존재하지않으면_400을_응답한다() {
        // given
        final ExtractableResponse<Response> response = SimpleRestAssured.post(MEMBER_BASE_PATH + "/baby", 멤버_가입_요청);

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 유효하지_않은_sign_토큰으로_요청_시_401을_응답한다() {
        // given
        final String invalidSignToken = "111";
        final ExtractableResponse<Response> response = post(
                MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + invalidSignToken), 멤버_가입_요청
        );

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void body에_잘못된_값이_존재하면_400을_던진다() {
        // given
        final MemberSignUpRequest INVALID_MEMBER_SIGN_UP_REQUEST = new MemberSignUpRequest(null, null, "엄마",
                List.of(new BabyRequest("아기1", LocalDate.of(2022, 1, 1)),
                        new BabyRequest("아기2", LocalDate.of(2023, 1, 1)))
        );
        final String validToken = signTokenProvider.createToken(MEMBER_ID);
        final ExtractableResponse<Response> response = post(
                MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + validToken),
                INVALID_MEMBER_SIGN_UP_REQUEST
        );

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 이미_가입한_유저가_회원가입을_요청하면_400을_던진다() {
        // given
        final String invalidSignToken = signTokenProvider.createToken(멤버1.getId());

        post(MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + invalidSignToken), 멤버_가입_요청);

        // when
        final ExtractableResponse<Response> response = post(
                MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + invalidSignToken), 멤버_가입_요청
        );

        //  then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 회원가입을_진행한다() {
        // given
        final String token = signTokenProvider.createToken(MEMBER_ID);

        // when
        final ExtractableResponse<Response> response = post(
                MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + token), 멤버_가입_요청
        );

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.as(MemberSignUpResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).refreshToken()).isNotBlank()
        );
    }

    @Test
    void 사용자_정보를_조회한다() {
        // given
        final String signToken = signTokenProvider.createToken(멤버1.getId());
        final String accessToken = toObject(post(MEMBER_BASE_PATH + "/baby",
                Map.of("Authorization", "Bearer " + signToken), 멤버_가입_요청), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response =
                get(MEMBER_BASE_PATH, Map.of("Authorization", "Bearer " + accessToken));

        // then
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, MemberResponse.class)).isEqualTo(
                        new MemberResponse(멤버_가입_요청.getName(), "", 멤버_가입_요청.getIconName(), IconColor.COLOR_1.name()))
        );
    }

    @Test
    void 존재하지않는_사용자_정보를_조회_시_404를_반환한다() {
        // given
        final String invalidAccessToken = accessTokenProvider.createToken("invalidMemberId");

        // when
        final ExtractableResponse<Response> response =
                get(MEMBER_BASE_PATH, Map.of("Authorization", "Bearer " + invalidAccessToken));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Picker<IconColor> picker() {
            return colors -> IconColor.COLOR_1;
        }
    }
}
