package com.baba.back;

import static com.baba.back.SimpleRestAssured.delete;
import static com.baba.back.SimpleRestAssured.get;
import static com.baba.back.SimpleRestAssured.patch;
import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.put;
import static com.baba.back.SimpleRestAssured.thenExtract;
import static com.baba.back.fixture.RequestFixture.그룹_멤버_정보_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.그룹_정보_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.그룹_추가_요청_데이터1;
import static com.baba.back.fixture.RequestFixture.그룹_추가_요청_데이터2;
import static com.baba.back.fixture.RequestFixture.마이_프로필_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청_데이터;
import static com.baba.back.fixture.RequestFixture.소셜_토큰_요청_데이터;
import static com.baba.back.fixture.RequestFixture.아기_이름_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.아기_추가_요청_데이터;
import static com.baba.back.fixture.RequestFixture.약관_동의_요청_데이터;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터1;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터2;

import com.baba.back.baby.dto.CreateInviteCodeRequest;
import com.baba.back.baby.dto.InviteCodeRequest;
import com.baba.back.content.dto.ContentUpdateTitleAndCardStyleRequest;
import com.baba.back.content.dto.CreateCommentRequest;
import com.baba.back.content.dto.UpdateContentPhotoRequest;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.service.SignTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class AcceptanceTest {

    private static final String BASE_PATH = "api";
    private static final String MEMBER_BASE_PATH = "members";
    private static final String AUTH_BASE_PATH = "auth";
    private static final String BABY_BASE_PATH = "baby";
    private static final String CONTENT_BASE_PATH = "album";

    @Autowired
    protected SignTokenProvider signTokenProvider;
    @LocalServerPort
    int port;

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청() {
        return 아기_등록_회원가입_요청(UUID.randomUUID().toString(), 멤버_가입_요청_데이터);
    }

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청(String memberId) {
        return 아기_등록_회원가입_요청(memberId, 멤버_가입_요청_데이터);
    }

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청(MemberSignUpRequest request) {
        return 아기_등록_회원가입_요청(UUID.randomUUID().toString(), request);
    }

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청(String memberId, MemberSignUpRequest request) {
        final String signToken = signTokenProvider.createToken(memberId);
        return post(String.format("/%s/%s/%s", BASE_PATH, MEMBER_BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + signToken), request);
    }

    protected ExtractableResponse<Response> 초대코드로_회원가입_요청(String memberId, String code) {
        final String signToken = signTokenProvider.createToken(memberId);
        return post(String.format("/%s/%s/%s/invite-code", BASE_PATH, MEMBER_BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + signToken),
                new SignUpWithCodeRequest(code, "박재희", "PROFILE_W_1"));
    }

    protected ExtractableResponse<Response> 외가_그룹_추가_요청(String accessToken) {
        return post(String.format("/%s/%s/groups", BASE_PATH, MEMBER_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), 그룹_추가_요청_데이터1);
    }

    protected ExtractableResponse<Response> 가족_그룹_추가_요청(String accessToken) {
        return post(String.format("/%s/%s/groups", BASE_PATH, MEMBER_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), 그룹_추가_요청_데이터2);
    }

    protected ExtractableResponse<Response> 그룹_정보_변경_요청(String accessToken) {
        return patch(String.format("/%s/%s/groups?groupName=%s", BASE_PATH, MEMBER_BASE_PATH, "외가"),
                Map.of("Authorization", "Bearer " + accessToken), 그룹_정보_변경_요청_데이터);
    }

    protected ExtractableResponse<Response> 그룹_멤버_정보_변경_요청(String accessToken, String memberId) {
        return patch(String.format("/%s/%s/groups/%s", BASE_PATH, MEMBER_BASE_PATH, memberId),
                Map.of("Authorization", "Bearer " + accessToken), 그룹_멤버_정보_변경_요청_데이터);
    }

    protected ExtractableResponse<Response> 외가_그룹_삭제_요청(String accessToken) {
        return delete(String.format("/%s/%s/groups?groupName=%s", BASE_PATH, MEMBER_BASE_PATH, "외가"),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 그룹_멤버_삭제_요청(String accessToken, String memberId) {
        return delete(String.format("/%s/%s/groups/%s", BASE_PATH, MEMBER_BASE_PATH, memberId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 초대장_조회_요청(String code) {
        return get(String.format("/%s/%s/invitation?code=%s", BASE_PATH, BABY_BASE_PATH, code));
    }

    protected ExtractableResponse<Response> 사용자_정보_요청(String accessToken) {
        return get(String.format("/%s/%s", BASE_PATH, MEMBER_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 마이_프로필_변경_요청(String accessToken) {
        return put(String.format("/%s/%s", BASE_PATH, MEMBER_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), 마이_프로필_변경_요청_데이터);
    }

    protected ExtractableResponse<Response> 소셜_로그인_요청() {
        return post(String.format("/%s/%s/login", BASE_PATH, AUTH_BASE_PATH), 소셜_토큰_요청_데이터);
    }

    protected ExtractableResponse<Response> 약관_조회_요청() {
        return post(String.format("/%s/%s/terms", BASE_PATH, AUTH_BASE_PATH), 소셜_토큰_요청_데이터);
    }

    protected ExtractableResponse<Response> 약관_동의_요청() {
        return post(String.format("/%s/%s/login/terms", BASE_PATH, AUTH_BASE_PATH), 약관_동의_요청_데이터);
    }

    protected ExtractableResponse<Response> 토큰_재발급_요청(TokenRefreshRequest request) {
        return post(String.format("/%s/%s/refresh", BASE_PATH, AUTH_BASE_PATH), request);
    }

    protected ExtractableResponse<Response> 아기_추가_요청(String accessToken) {
        return post(String.format("/%s/%s", BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), 아기_추가_요청_데이터);
    }

    protected ExtractableResponse<Response> 초대코드로_아기_추가_요청(String accessToken, String code) {
        return post(String.format("/%s/%s/code", BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), new InviteCodeRequest(code));
    }

    protected ExtractableResponse<Response> 아기_리스트_조회_요청(String accessToken) {
        return get(String.format("/%s/%s", BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 아기_이름_변경_요청(String accessToken, String babyId) {
        return patch(String.format("/%s/%s/%s", BASE_PATH, BABY_BASE_PATH, babyId),
                Map.of("Authorization", "Bearer " + accessToken), 아기_이름_변경_요청_데이터);
    }

    protected ExtractableResponse<Response> 아기_삭제_요청(String accessToken, String babyId) {
        return delete(String.format("/%s/%s/%s", BASE_PATH, BABY_BASE_PATH, babyId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 성장앨범_생성_요청(String accessToken, String babyId, LocalDate now) {
        return thenExtract(
                SimpleRestAssured.given()
                        .headers(Map.of("Authorization", "Bearer " + accessToken))
                        .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                        .multiPart("date", now.toString())
                        .multiPart("title", "title")
                        .multiPart("cardStyle", "CARD_BASIC_1")
                        .when()
                        .post(String.format("/%s/%s/%s/%s", BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH))
        );
    }

    protected ExtractableResponse<Response> 좋아요_요청(String accessToken, String babyId, Long contentId) {
        return post(String.format("/%s/%s/%s/%s/%s/like",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId.toString()),
                Map.of("Authorization", "Bearer " + accessToken)
        );
    }

    protected ExtractableResponse<Response> 댓글_생성_요청(String accessToken, String babyId, Long contentId,
                                                     CreateCommentRequest request) {
        return post(
                String.format("/%s/%s/%s/%s/%s/comment", BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH,
                        contentId.toString()),
                Map.of("Authorization", "Bearer " + accessToken),
                request
        );
    }

    protected ExtractableResponse<Response> 성장_앨범_메인_요청(String accessToken, String babyId, int year, int month) {
        return get(String.format("/%s/%s/%s/%s?year=%d&month=%d",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, year, month),
                Map.of("Authorization", "Bearer " + accessToken)
        );
    }

    protected ExtractableResponse<Response> 가족_초대_코드_생성_요청(String accessToken) {
        return 초대_코드_생성_요청(accessToken, 초대코드_생성_요청_데이터2);
    }

    protected ExtractableResponse<Response> 외가_초대_코드_생성_요청(String accessToken) {
        return 초대_코드_생성_요청(accessToken, 초대코드_생성_요청_데이터1);
    }

    private ExtractableResponse<Response> 초대_코드_생성_요청(String accessToken, CreateInviteCodeRequest request) {
        return post(String.format("/%s/%s/invite-code", BASE_PATH, BABY_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken), request);
    }

    protected ExtractableResponse<Response> 성장앨범_댓글_보기_요청(String accessToken, String babyId, Long contentId) {
        return get(String.format("/%s/%s/%s/%s/%s/comments",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 성장_앨범_좋아요_보기_요청(String accessToken, String babyId, Long contentId) {
        return get(String.format("/%s/%s/%s/%s/%s/likes",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 마이_그룹별_조회_요청(String accessToken) {
        return get(String.format("/%s/%s/my-page", BASE_PATH, MEMBER_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 다른_아기_프로필_조회_요청(String accessToken, String babyId) {
        return get(String.format("/%s/%s/baby-page/%s", BASE_PATH, MEMBER_BASE_PATH, babyId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 성장_앨범_제목_카드_수정_요청(String accessToken, String babyId, Long contentId,
                                                              ContentUpdateTitleAndCardStyleRequest request) {
        return patch(String.format("/%s/%s/%s/%s/%s/title-card",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId),
                Map.of("Authorization", "Bearer " + accessToken),
                request);
    }

    protected ExtractableResponse<Response> 댓글_삭제_요청(String accessToken, String babyId, Long contentId,
                                                     Long commentId) {
        return delete(String.format("/%s/%s/%s/%s/%s/comment/%s",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId, commentId),
                Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 성장_앨범_사진_수정_요청(String accessToken, String babyId, Long contentId,
                                                           UpdateContentPhotoRequest request) throws IOException {
        final MultipartFile photo = request.photo();
        return thenExtract(
                SimpleRestAssured.given()
                        .headers(Map.of("Authorization", "Bearer " + accessToken))
                        .multiPart("photo", photo.getOriginalFilename(), photo.getBytes(),
                                photo.getContentType())
                        .when()
                        .patch(String.format("/%s/%s/%s/%s/%s/photo",
                                BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH, contentId))
        );
    }

    protected ExtractableResponse<Response> 성장_앨범_모두_보기_요청(String accessToken, String babyId) {
        return get(String.format("/%s/%s/%s/%s/all",
                        BASE_PATH, BABY_BASE_PATH, babyId, CONTENT_BASE_PATH),
                Map.of("Authorization", "Bearer " + accessToken)
        );
    }

    protected Long getContentId(ExtractableResponse<Response> response) {
        final String location = getLocation(response);
        final String id = location.split("/")[4];
        return Long.parseLong(id);
    }

    protected String getBabyId(ExtractableResponse<Response> response) {
        final String location = getLocation(response);
        return location.split("/")[2];
    }

    protected Long getCommentId(ExtractableResponse<Response> response) {
        final String location = getLocation(response);
        final String id = location.split("/")[6];
        return Long.parseLong(id);
    }

    protected String getLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

}
