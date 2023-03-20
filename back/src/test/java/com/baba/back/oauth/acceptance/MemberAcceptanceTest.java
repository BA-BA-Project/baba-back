package com.baba.back.oauth.acceptance;

import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.service.AccessTokenProvider;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class MemberAcceptanceTest extends AcceptanceTest {

    private static final String MEMBER_ID = "memberId";

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "invalidToken")
    void 아기_등록_회원가입_요청에_유효하지_않은_sign_토큰으로_요청_시_401을_응답한다(String invalidSignToken) {
        // given
        final ExtractableResponse<Response> response = post("/api/members/baby",
                Map.of("Authorization", "Bearer " + invalidSignToken), 멤버_가입_요청_데이터);

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입_요청에_body에_잘못된_값이_존재하면_400을_던진다() {
        // given
        final MemberSignUpRequest invalidMemberSignUpRequest = new MemberSignUpRequest(null, null, "엄마",
                List.of(new BabyRequest("아기1", LocalDate.of(2022, 1, 1)),
                        new BabyRequest("아기2", LocalDate.of(2023, 1, 1)))
        );
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청(invalidMemberSignUpRequest);

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입_요청_시_이미_가입한_유저가_회원가입을_요청하면_400을_던진다() {
        // given
        아기_등록_회원가입_요청();

        // when
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청();

        //  then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입을_진행한다() {
        // when
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getBabyId(response)).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).refreshToken()).isNotBlank()
        );
    }

    @Test
    void 사용자_정보를_조회한다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 사용자_정보_요청(accessToken);

        // then
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, MemberResponse.class)).isEqualTo(
                        new MemberResponse(멤버_가입_요청_데이터.getName(), "", 멤버_가입_요청_데이터.getIconName(), Color.COLOR_1.name())
                )
        );
    }

    @Test
    void 존재하지않는_사용자_정보를_조회_시_404를_반환한다() {
        // given
        final String invalidAccessToken = accessTokenProvider.createToken("invalidMemberId");

        // when
        final ExtractableResponse<Response> response = 사용자_정보_요청(invalidAccessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Picker<Color> picker() {
            return colors -> Color.COLOR_1;
        }
    }
}
