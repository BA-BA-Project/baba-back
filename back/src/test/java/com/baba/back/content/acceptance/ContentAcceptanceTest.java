package com.baba.back.content.acceptance;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.컨텐츠;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.baba.back.AcceptanceTest;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.content.repository.ContentRepository;
import com.baba.back.content.repository.LikeRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.service.TokenProvider;
import com.baba.back.relation.repository.RelationRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ContentAcceptanceTest extends AcceptanceTest {

    public static final String BASE_PATH = "/api/album";
    public static final String VALID_URL = "http://test";

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

    @Autowired
    private LikeRepository likeRepository;

    @MockBean
    private AmazonS3 amazonS3;

    @AfterEach
    void tearDown() {
        likeRepository.deleteAll();
        contentRepository.deleteAll();
        relationRepository.deleteAll();
        babyRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 요청_body에_null값이_있으면_400을_던진다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                .multiPart("date", LocalDate.of(2023, 1, 25).toString())
                .multiPart("cardStyle", "card_basic_1")
                .when()
                .post(Paths.get(BASE_PATH, 아기1.getId()).toString())
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void AWS_자체_오류로_S3에_파일_업로드_실패시_500을_던진다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        memberRepository.save(멤버1);
        babyRepository.save(아기1);
        relationRepository.save(관계1);

        given(amazonS3.putObject(any())).willThrow(AmazonServiceException.class);

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                .multiPart("date", LocalDate.of(2023, 1, 25).toString())
                .multiPart("title", "제목")
                .multiPart("cardStyle", "card_basic_1")
                .when()
                .post(Paths.get(BASE_PATH, 아기1.getId()).toString())
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 컨텐츠를_생성한다() throws MalformedURLException {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        memberRepository.save(멤버1);
        babyRepository.save(아기1);
        relationRepository.save(관계1);

        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
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

    @Test
    void 좋아요를_추가한다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        memberRepository.save(멤버1);
        babyRepository.save(아기1);
        relationRepository.save(관계1);
        contentRepository.save(컨텐츠);

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .headers(Map.of("Authorization", "Bearer " + token))
                .when()
                .post(Paths.get(BASE_PATH, 아기1.getId(), 컨텐츠.getId().toString(), "like").toString())
                .then()
                .log().all()
                .extract();

        final Boolean isLiked = response.response().jsonPath().get("isLiked");

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(isLiked).isTrue()
        );
    }
}
