package com.baba.back;

import static com.baba.back.SimpleRestAssured.get;
import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;

import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.service.SignTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class AcceptanceTest {

    @Autowired
    protected SignTokenProvider signTokenProvider;
    private static final String BASE_PATH = "/api";
    private static final String MEMBER_BASE_PATH = BASE_PATH + "/members";

    @LocalServerPort
    int port;

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청_멤버_1() {
        final String signToken = signTokenProvider.createToken(멤버1.getId());
        return 아기_등록_회원가입_요청(signToken, 멤버_가입_요청);
    }

    protected ExtractableResponse<Response> 아기_등록_회원가입_요청(String signToken, MemberSignUpRequest request) {
        return post(MEMBER_BASE_PATH + "/baby", Map.of("Authorization", "Bearer " + signToken), request);
    }

    protected ExtractableResponse<Response> 사용자_정보_요청(String accessToken) {
        return get(MEMBER_BASE_PATH, Map.of("Authorization", "Bearer " + accessToken));
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

}
