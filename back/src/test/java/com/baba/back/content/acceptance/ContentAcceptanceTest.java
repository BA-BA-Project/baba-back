package com.baba.back.content.acceptance;

import static com.baba.back.SimpleRestAssured.thenExtract;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.service.AccessTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ContentAcceptanceTest extends AcceptanceTest {

    public static final String VALID_URL = "http://test";

    @Autowired
    private AccessTokenProvider tokenProvider;

    @MockBean
    private AmazonS3 amazonS3;

    @Test
    void 요청_body에_null값이_있으면_400을_던진다() {
        // given
        final String token = tokenProvider.createToken(멤버1.getId());

        // when
        final ExtractableResponse<Response> response = thenExtract(
                RestAssured.given()
                        .headers(Map.of("Authorization", "Bearer " + token))
                        .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                        .multiPart("date", LocalDate.now())
                        .multiPart("cardStyle", "card_basic_1")
                        .when()
                        .post("/api/album/" +  아기1.getId())
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void AWS_자체_오류로_S3에_파일_업로드_실패시_500을_던진다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();
        final String babyId = toObject(아기_리스트_조회_요청(accessToken), BabiesResponse.class).myBaby().get(0).babyId();
        given(amazonS3.putObject(any())).willThrow(AmazonServiceException.class);

        // when
        final ExtractableResponse<Response> response = 성장앨범_생성_요청(accessToken, babyId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 컨텐츠를_생성한다() throws MalformedURLException {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();
        final String babyId = toObject(아기_리스트_조회_요청(accessToken), BabiesResponse.class).myBaby().get(0).babyId();
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));

        // when
        final ExtractableResponse<Response> response = 성장앨범_생성_요청(accessToken, babyId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getContentId(response)).isPositive()
        );

    }

    @Test
    void 좋아요를_처음_누르면_좋아요가_추가된다() throws MalformedURLException {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();
        final String babyId = toObject(아기_리스트_조회_요청(accessToken), BabiesResponse.class).myBaby().get(0).babyId();
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId));

        // when
        final ExtractableResponse<Response> response = 좋아요_요청(accessToken, babyId, contentId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, LikeContentResponse.class).isLiked()).isTrue()
        );
    }

    @Test
    void 좋아요를_처음_누르고_한번_더_누르면_기존의_좋아요가_취소된다() throws MalformedURLException {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();
        final String babyId = toObject(아기_리스트_조회_요청(accessToken), BabiesResponse.class).myBaby().get(0).babyId();
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId));
        좋아요_요청(accessToken, babyId, contentId);

        // when
        final ExtractableResponse<Response> response = 좋아요_요청(accessToken, babyId, contentId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, LikeContentResponse.class).isLiked()).isFalse()
        );
    }

    @Test
    void 좋아요를_취소하고_한번_더_누르면_다시_좋아요가_된다() throws MalformedURLException {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청_멤버_1(), MemberSignUpResponse.class).accessToken();
        final String babyId = toObject(아기_리스트_조회_요청(accessToken), BabiesResponse.class).myBaby().get(0).babyId();
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId));
        좋아요_요청(accessToken, babyId, contentId);
        좋아요_요청(accessToken, babyId, contentId);

        // when
        final ExtractableResponse<Response> response = 좋아요_요청(accessToken, babyId, contentId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, LikeContentResponse.class).isLiked()).isTrue()
        );
    }
}
