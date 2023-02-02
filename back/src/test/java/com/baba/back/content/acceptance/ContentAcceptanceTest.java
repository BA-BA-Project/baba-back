package com.baba.back.content.acceptance;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.TokenProvider;
import com.baba.back.relation.repository.RelationRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class ContentAcceptanceTest extends AcceptanceTest {

    public static final String BASE_PATH = "/api/album";

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private ContentRepository contentRepository;

    @AfterEach
    void tearDown() {
        contentRepository.deleteAll();
        relationRepository.deleteAll();
        babyRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 요청_body에_잘못된_값이_있으면_400을_던진다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .multiPart("photo", "origin.txt", "Something".getBytes(), "multipart/form-data")
                .when()
                .post(Paths.get(BASE_PATH, 아기1.getId()).toString())
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 컨텐츠를_생성한다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        memberRepository.save(멤버1);
        babyRepository.save(아기1);
        relationRepository.save(관계1);

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .multiPart("photo", "test_file.jpg", "Something".getBytes(), "image/jpeg")
                .multiPart("date", LocalDate.of(2023, 1, 25).toString())
                .multiPart("title", "제목")
                .multiPart("cardStyle", "card_basic_1")
                .when()
                .post(Paths.get(BASE_PATH, 아기1.getId()).toString())
                .then()
                .log().all()
                .extract();

        final Boolean isSuccess = response.response().jsonPath().get("isSuccess");
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(isSuccess).isTrue()
        );

    }

}
