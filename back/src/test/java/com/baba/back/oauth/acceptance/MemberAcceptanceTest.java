package com.baba.back.oauth.acceptance;

import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.oauth.domain.JoinedMember;
import com.baba.back.oauth.dto.MemberJoinRequest;
import com.baba.back.oauth.dto.MemberJoinResponse;
import com.baba.back.oauth.repository.JoinedMemberRepository;
import com.baba.back.oauth.service.MemberTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MemberAcceptanceTest extends AcceptanceTest {

    private static final String MEMBER_BASE_PATH = "/api/members/baby";
    private static final String MEMBER_ID = "memberId";
    @Autowired
    private MemberTokenProvider tokenProvider;
    @Autowired
    private JoinedMemberRepository joinedMemberRepository;

    @Test
    void 요청에_토큰이_존재하지않으면_400을_응답한다() {
        // given
        final ExtractableResponse<Response> response = RestAssured.given()
                .body(멤버_가입_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 유효하지_않은_토큰으로_요청_시_401을_응답한다() {
        // given
        final String invalidToken = "111";
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + invalidToken))
                .body(멤버_가입_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void body에_잘못된_값이_존재하면_400을_던진다() {
        // given
        final MemberJoinRequest INVALID_MEMBER_JOIN_REQUEST = new MemberJoinRequest(null, null, "엄마",
                List.of(new BabyRequest("아기1", LocalDate.of(2022, 1, 1)),
                        new BabyRequest("아기2", LocalDate.of(2023, 1, 1)))
        );
        final String validToken = tokenProvider.createToken(MEMBER_ID);
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + validToken))
                .body(INVALID_MEMBER_JOIN_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 이미_가입한_유저가_회원가입을_요청하면_400을_던진다() {
        // given
        final String validToken = tokenProvider.createToken(MEMBER_ID);
        joinedMemberRepository.save(new JoinedMember(MEMBER_ID, false));

        RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + validToken))
                .body(멤버_가입_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + validToken))
                .body(멤버_가입_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        //  then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 회원가입을_진행한다() {
        // given
        final String token = tokenProvider.createToken(MEMBER_ID);
        joinedMemberRepository.save(new JoinedMember(MEMBER_ID, false));

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .body(멤버_가입_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(MEMBER_BASE_PATH)
                .then()
                .log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.as(MemberJoinResponse.class).result()).isNotBlank()
        );
    }
}
