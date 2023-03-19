package com.baba.back.baby.invitation.acceptance;

import static com.baba.back.SimpleRestAssured.toObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class InvitationAcceptanceTest extends AcceptanceTest {

    @Test
    void 초대_코드_생성_요청_시_초대_코드를_생성한다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 초대_코드_생성_요청(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, CreateInviteCodeResponse.class).inviteCode()).isNotBlank()
        );
    }

    // TODO: 2023/03/16 관계그룹 생성 로직 추가 이후 다른 그룹의 초대 코드 생성 테스트를 추가한다.
}
