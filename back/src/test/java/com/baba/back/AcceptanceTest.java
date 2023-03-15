package com.baba.back;

import static com.baba.back.SimpleRestAssured.get;
import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.thenExtract;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청_데이터;
import static com.baba.back.fixture.RequestFixture.소셜_토큰_요청_데이터;
import static com.baba.back.fixture.RequestFixture.약관_동의_요청_데이터;

import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.service.SignTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class AcceptanceTest {

    private static final String BASE_PATH = "/api";
    private static final String MEMBER_BASE_PATH = BASE_PATH + "/members";
    private static final String AUTH_BASE_PATH = BASE_PATH + "/auth";
    private static final String BABY_BASE_PATH = BASE_PATH + "/baby";
    private static final String CONTENT_BASE_PATH = BASE_PATH + "/album";

    @Autowired
    protected SignTokenProvider signTokenProvider;
    @LocalServerPort
    int port;

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청() {
        return 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
    }

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청(MemberSignUpRequest request) {
        final String signToken = signTokenProvider.createToken("member");
        return post(MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + signToken), request);
    }

    protected ExtractableResponse<Response> 사용자_정보_요청(String accessToken) {
        return get(MEMBER_BASE_PATH, Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 소셜_로그인_요청() {
        return post(AUTH_BASE_PATH + "/login", 소셜_토큰_요청_데이터);
    }

    protected ExtractableResponse<Response> 약관_조회_요청() {
        return post(AUTH_BASE_PATH + "/terms", 소셜_토큰_요청_데이터);
    }

    protected ExtractableResponse<Response> 약관_동의_요청() {
        return post(AUTH_BASE_PATH + "/login/terms", 약관_동의_요청_데이터);
    }

    protected ExtractableResponse<Response> 토큰_재발급_요청(TokenRefreshRequest request) {
        return post(AUTH_BASE_PATH + "/refresh", request);
    }

    protected ExtractableResponse<Response> 아기_리스트_조회_요청(String accessToken) {
        return get(BABY_BASE_PATH, Map.of("Authorization", "Bearer " + accessToken));
    }

    protected ExtractableResponse<Response> 성장앨범_생성_요청(String accessToken, String babyId, LocalDate now) {
        return thenExtract(
                RestAssured.given()
                        .headers(Map.of("Authorization", "Bearer " + accessToken))
                        .multiPart("photo", "test_file.jpg", "Something".getBytes(), MediaType.IMAGE_PNG_VALUE)
                        .multiPart("date", now.toString())
                        .multiPart("title", "title")
                        .multiPart("cardStyle", "CARD_BASIC_1")
                        .when()
                        .post(CONTENT_BASE_PATH + "/" + babyId)
        );
    }

    protected ExtractableResponse<Response> 좋아요_요청(String accessToken, String babyId, Long contentId) {
        return post(
                Paths.get(CONTENT_BASE_PATH, babyId, contentId.toString(), "like").toString(),
                Map.of("Authorization", "Bearer " + accessToken)
        );
    }

    protected ExtractableResponse<Response> 성장_앨범_메인_요청(String accessToken, String babyId, int year, int month) {
        return get(
                CONTENT_BASE_PATH + "/" + babyId + "?year=" + year + "&month=" + month,
                Map.of("Authorization", "Bearer " + accessToken)
        );
    }

    protected Long getContentId(ExtractableResponse<Response> response) {
        final String location = getLocation(response);
        final String id = location.split("/")[3];
        return Long.parseLong(id);
    }

    protected String getBabyId(ExtractableResponse<Response> response) {
        final String location = getLocation(response);
        return location.split("/")[2];
    }

    protected String getLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

}
