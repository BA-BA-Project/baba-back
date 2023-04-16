package com.baba.back.baby.acceptance;

import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.DomainFixture.nowDate;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeBabyResponse;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class BabyAcceptanceTest extends AcceptanceTest {

    @Nested
    class 아기_추가_요청_시_ {

        final String memberId = "memberId";

        @Test
        void 자신의_아기가_없다면_아기를_추가한다() {
            // given
            final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();
            그룹_추가_요청(accessToken);
            final String inviteCode = toObject(외가_초대_코드_생성_요청(accessToken),
                    CreateInviteCodeResponse.class).inviteCode();
            final String token = toObject(초대코드로_회원가입_요청(memberId, inviteCode),
                    MemberSignUpResponse.class).accessToken();

            // when
            final ExtractableResponse<Response> response = 아기_추가_요청(token);
            final String babyId = getBabyId(response);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(babyId).isNotBlank()
            );
        }

        @Test
        void 자신의_아기가_있어도_아기를_추가한다() {
            // given
            final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();

            // when
            final ExtractableResponse<Response> response = 아기_추가_요청(accessToken);
            final String babyId = getBabyId(response);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(babyId).isNotBlank()
            );
        }
    }

    // TODO: 2023/03/09 아기 초대 API 생성 후 다른 아기 추가하여 테스트 진행한다.
    @Test
    void 아기_리스트_요청_시_등록된_아기가_조회된다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 아기_리스트_조회_요청(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, BabiesResponse.class))
                        .usingRecursiveComparison()
                        .ignoringFields("myBaby.babyId", "others.babyId")
                        .isEqualTo(
                                new BabiesResponse(
                                        List.of(
                                                new BabyResponse(아기1.getId(), Color.COLOR_1.getValue(), 아기1.getName()),
                                                new BabyResponse(아기2.getId(), Color.COLOR_1.getValue(), 아기2.getName())
                                        ),
                                        List.of()
                                )
                        )
        );
    }

    @Test
    void 아기_이름_변경_요청_시_아기_이름이_변경된다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청();
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(아기_등록_회원가입_응답);

        // when
        final ExtractableResponse<Response> response = 아기_이름_변경_요청(accessToken, babyId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 초대_코드_생성_요청_시_초대_코드를_생성한다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 가족_초대_코드_생성_요청(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, CreateInviteCodeResponse.class).inviteCode()).isNotBlank()
        );
    }

    @Test
    void 초대코드_생성_요청시_소속그룹과_관계명이_동일한_초대코드가_이미_존재하면_초대코드를_업데이트_한다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();
        가족_초대_코드_생성_요청(accessToken);

        // when
        final ExtractableResponse<Response> response = 가족_초대_코드_생성_요청(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(toObject(response, CreateInviteCodeResponse.class).inviteCode()).isNotBlank()
        );
    }

    // TODO: 2023/03/16 관계그룹 생성 로직 추가 이후 다른 그룹의 초대 코드 생성 테스트를 추가한다.
    @Test
    void 초대_코드_생성_요청_시_가족_그룹_이외의_초대_코드도_생성할수_있다() {

    }

    @Test
    void 초대_코드_조회_요청_시_저장된_초대_코드_정보를_응답한다() {
        // given
        final String accessToken = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();
        final String code = toObject(가족_초대_코드_생성_요청(accessToken), CreateInviteCodeResponse.class).inviteCode();

        // when
        final ExtractableResponse<Response> response = 초대장_조회_요청(code);

        // then
        final SearchInviteCodeResponse codeResponse = toObject(response, SearchInviteCodeResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(codeResponse.relationName()).isEqualTo(초대코드_생성_요청_데이터2.getRelationName()),
                () -> assertThat(codeResponse.babies().stream().map(InviteCodeBabyResponse::babyName).toList())
                        .containsExactly(아기1.getName(), 아기2.getName())
        );
    }

    // TODO: 2023/03/20 관계그룹 생성 로직 추가 이후 다른 그룹의 초대 코드 조회 테스트를 추가한다.
    @Test
    void 초대_코드_조회_요청_시_가족_그룹_이외의_초대_코드도_조회할_수_있다() {

    }

    @Test
    void 초대코드로_아기_추가_요청_시_아기의_성장앨범을_조회할_수_있다() {
        // given
        final ExtractableResponse<Response> signUpResponse = 아기_등록_회원가입_요청();
        final String accessToken = toObject(signUpResponse, MemberSignUpResponse.class).accessToken();
        final String babyId = getBabyId(signUpResponse);

        그룹_추가_요청(accessToken);
        final String inviteCode = toObject(외가_초대_코드_생성_요청(accessToken),
                CreateInviteCodeResponse.class).inviteCode();

        final String accessToken2 = toObject(아기_등록_회원가입_요청(), MemberSignUpResponse.class).accessToken();
        초대코드로_아기_추가_요청(accessToken2, inviteCode);

        // when
        final ExtractableResponse<Response> response = 성장_앨범_메인_요청(accessToken2, babyId, nowDate.getYear(),
                nowDate.getMonthValue());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Picker<Color> picker() {
            return colors -> Color.COLOR_1;
        }
    }
}
