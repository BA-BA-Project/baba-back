package com.baba.back.content.acceptance;

import static com.baba.back.SimpleRestAssured.thenExtract;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.nowDate;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.baba.back.AcceptanceTest;
import com.baba.back.content.dto.CommentsResponse;
import com.baba.back.content.dto.ContentResponse;
import com.baba.back.content.dto.ContentsResponse;
import com.baba.back.content.dto.CreateCommentRequest;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.service.AccessTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ContentAcceptanceTest extends AcceptanceTest {

    public static final String VALID_URL = "http://test";

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @MockBean
    private AmazonS3 amazonS3;

    @Test
    void 요청_body에_null값이_있으면_400을_던진다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = thenExtract(
                RestAssured.given()
                        .headers(Map.of("Authorization", "Bearer " + accessToken))
                        .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                        .multiPart("date", LocalDate.now())
                        .multiPart("cardStyle", "card_basic_1")
                        .when()
                        .post("/api/album/" + 아기1.getId())
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void AWS_자체_오류로_S3에_파일_업로드_실패시_500을_던진다() {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.putObject(any())).willThrow(AmazonServiceException.class);

        // when
        final ExtractableResponse<Response> response = 성장앨범_생성_요청(accessToken, babyId, nowDate);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 컨텐츠를_생성한다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));

        // when
        final ExtractableResponse<Response> response = 성장앨범_생성_요청(accessToken, babyId, nowDate);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getContentId(response)).isPositive()
        );

    }

    @Test
    void 좋아요를_처음_누르면_좋아요가_추가된다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);

        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId, nowDate));

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
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId, nowDate));
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
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId, nowDate));
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

    @Test
    void 원하는_년_월의_성장_앨범을_조회한다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        성장앨범_생성_요청(accessToken, babyId, nowDate);
        성장앨범_생성_요청(accessToken, babyId, nowDate.minusDays(1));
        성장앨범_생성_요청(accessToken, babyId, nowDate.minusDays(2));
        성장앨범_생성_요청(accessToken, babyId, nowDate.minusDays(3));

        // when
        final ExtractableResponse<Response> response = 성장_앨범_메인_요청(
                accessToken, babyId, nowDate.getYear(), nowDate.getMonthValue()
        );

        // then
        final List<ContentResponse> album = toObject(response, ContentsResponse.class).album();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(album).hasSize(4),
                () -> assertThat(album.stream().map(ContentResponse::date).toList())
                        .containsExactly(
                                nowDate.minusDays(3),
                                nowDate.minusDays(2),
                                nowDate.minusDays(1),
                                nowDate
                        )
        );
    }

    // TODO: 2023/03/12 초대 로직 생성 후 테스트 작성
    @Test
    void 원하는_년_월의_내가_올린_성장_앨범과_다른_사람이_올린_성장_앨범을_조회한다() {
    }

    @Test
    void 태그를_하지않고_댓글을_추가할_수_있다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId, nowDate));

        // when
        final ExtractableResponse<Response> response =
                댓글_생성_요청(accessToken, babyId, contentId, new CreateCommentRequest("", "안녕"));

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getCommentId(response)).isPositive()
        );
    }

    // TODO: 2023/03/21 멤버 초대 API 구현 후 작성
    @Test
    void 태그를_하고_댓글을_추가할_수_있다() {
    }

    @Test
    void 아기와_관계없는_멤버를_태그한_댓글을_추가할_수_없다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse1 = 아기_등록_회원가입_요청();
        final String accessToken1 = toObject(signUpResponse1, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse1);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken1, babyId, nowDate));

        // given
        final ExtractableResponse<Response> signUpResponse2 = 아기_등록_회원가입_요청();
        final String accessToken2 = toObject(signUpResponse2, MemberSignUpResponse.class).accessToken();
        final String member2Id = accessTokenProvider.parseToken(accessToken2);

        // when
        final ExtractableResponse<Response> response =
                댓글_생성_요청(accessToken1, babyId, contentId, new CreateCommentRequest(member2Id, "안녕"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 성장_앨범_댓글을_볼_수_있다() throws MalformedURLException {
        // given
        final ExtractableResponse<Response> signUpResponse1 = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse1, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse1);
        given(amazonS3.getUrl(any(String.class), any(String.class))).willReturn(new URL(VALID_URL));
        final Long contentId = getContentId(성장앨범_생성_요청(accessToken, babyId, nowDate));
        댓글_생성_요청(accessToken, babyId, contentId, new CreateCommentRequest("", "테스트"));
        댓글_생성_요청(accessToken, babyId, contentId, new CreateCommentRequest("", "짜기싫다"));

        // when
        final ExtractableResponse<Response> httpResponse = 성장앨범_댓글_보기_요청(accessToken, contentId);

        // then
        final CommentsResponse response = toObject(httpResponse, CommentsResponse.class);
        assertAll(
                () -> assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.comments()).hasSize(2)
        );
    }

    // TODO: 2023/03/26 멤버 초대 API 구현되면 테스트 작성
    @Test
    void 성장_앨범_자세히_보기_요청_시_가족_멤버가_요청하면_모든_좋아요_댓글을_확인할_수_있다() {
    }

    @Test
    void 성장_앨범_자세히_보기_요청_시_가족이_아닌_멤버가_요청하면_가족_및_소속_그룹의_좋아요_댓글만_확인할_수_있다() {
    }
}
