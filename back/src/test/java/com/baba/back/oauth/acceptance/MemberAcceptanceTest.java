package com.baba.back.oauth.acceptance;

import static com.baba.back.SimpleRestAssured.post;
import static com.baba.back.SimpleRestAssured.toObject;
import static com.baba.back.fixture.RequestFixture.마이_프로필_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.AcceptanceTest;
import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.common.dto.ExceptionResponse;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.dto.BabyProfileResponse;
import com.baba.back.oauth.dto.GroupMemberResponse;
import com.baba.back.oauth.dto.GroupResponseWithFamily;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.MyProfileResponse;
import com.baba.back.oauth.service.AccessTokenProvider;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class MemberAcceptanceTest extends AcceptanceTest {

    private static final String MEMBER_ID = "memberId";

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "invalidToken")
    void 아기_등록_회원가입_요청에_유효하지_않은_sign_토큰으로_요청_시_401을_응답한다(String invalidSignToken) {
        // given
        final ExtractableResponse<Response> response = post("/api/members/baby",
                Map.of("Authorization", "Bearer " + invalidSignToken), 멤버_가입_요청_데이터);

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입_요청에_body에_잘못된_값이_존재하면_400을_던진다() {
        // given
        final MemberSignUpRequest invalidMemberSignUpRequest = new MemberSignUpRequest(null, null, "엄마",
                List.of(new BabyRequest("아기1", LocalDate.of(2022, 1, 1)),
                        new BabyRequest("아기2", LocalDate.of(2023, 1, 1)))
        );
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청(invalidMemberSignUpRequest);

        // when & then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입_요청_시_이미_가입한_유저가_회원가입을_요청하면_400을_던진다() {
        // given
        final String memberId = "memberId";
        아기_등록_회원가입_요청(memberId);

        // when
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청(memberId);

        //  then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 아기_등록_회원가입을_진행한다() {
        // when
        final ExtractableResponse<Response> response = 아기_등록_회원가입_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getBabyId(response)).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).refreshToken()).isNotBlank()
        );
    }

    @Test
    void 사용자_정보를_조회한다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 사용자_정보_요청(accessToken);

        // then
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(toObject(response, MemberResponse.class)).extracting("name", "introduction",
                                "iconName", "iconColor")
                        .containsExactly(멤버_가입_요청_데이터.getName(), "", 멤버_가입_요청_데이터.getIconName(),
                                Color.COLOR_1.getValue())
        );
    }

    @Test
    void 존재하지않는_사용자_정보를_조회_시_404를_반환한다() {
        // given
        final String invalidAccessToken = accessTokenProvider.createToken("invalidMemberId");

        // when
        final ExtractableResponse<Response> response = 사용자_정보_요청(invalidAccessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 마이_프로필_변경_요청_시_멤버_정보를_변경한다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> updateResponse = 마이_프로필_변경_요청(accessToken);

        // then
        final MemberResponse response = toObject(사용자_정보_요청(accessToken), MemberResponse.class);
        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response).extracting("name", "introduction", "iconName", "iconColor")
                        .containsExactly(마이_프로필_변경_요청_데이터.getName(), 마이_프로필_변경_요청_데이터.getIntroduction(),
                                마이_프로필_변경_요청_데이터.getIconName(),
                                마이_프로필_변경_요청_데이터.getIconColor())
        );
    }

    @Test
    void 초대코드로_회원가입을_진행한다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        final ExtractableResponse<Response> 가족_초대_코드_생성_응답 = 가족_초대_코드_생성_요청(accessToken);
        final String code = toObject(가족_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

        // when
        final ExtractableResponse<Response> response = 초대코드로_회원가입_요청(MEMBER_ID, code);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(getBabyId(response)).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).accessToken()).isNotBlank(),
                () -> assertThat(response.as(MemberSignUpResponse.class).refreshToken()).isNotBlank()
        );
    }

    @Test
    void 초대코드로_회원가입_요청_시_이미_회원가입_되어있으면_400을_던진다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        final ExtractableResponse<Response> 가족_초대_코드_생성_응답 = 가족_초대_코드_생성_요청(accessToken);
        final String code = toObject(가족_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

        초대코드로_회원가입_요청(MEMBER_ID, code);

        // when
        final ExtractableResponse<Response> response = 초대코드로_회원가입_요청(MEMBER_ID, code);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(toObject(response, ExceptionResponse.class).message()).isNotBlank()
        );
    }

    @Test
    void 그룹_추가_요청_시_그룹을_생성하고_201을_응답한다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

        // when
        final ExtractableResponse<Response> response = 외가_그룹_추가_요청(accessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 그룹_추가_요청시_동일한_그룹을_생성한다면_400을_던진다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        외가_그룹_추가_요청(accessToken);

        // when
        final ExtractableResponse<Response> response = 외가_그룹_추가_요청(accessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Nested
    class 마이_그룹별_조회_요청_시_ {

        @Test
        void 자신의_아기가_없으면_404를_던진다() {
            // given
            final String memberId1 = "memberId1";
            final String memberId2 = "memberId2";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);
            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

            외가_그룹_추가_요청(accessToken);

            final ExtractableResponse<Response> 가족_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
            final String code = toObject(가족_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

            final ExtractableResponse<Response> 초대코드로_회원가입_응답 = 초대코드로_회원가입_요청(memberId2, code);
            final String invitedMemberAccessToken = toObject(초대코드로_회원가입_응답, MemberSignUpResponse.class).accessToken();

            // when
            final ExtractableResponse<Response> response = 마이_그룹별_조회_요청(invitedMemberAccessToken);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 가족_그룹의_멤버들을_조회한다() {
            // given
            final String memberId1 = "memberId1";
            final String memberId2 = "memberId2";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);
            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

            final ExtractableResponse<Response> 가족_초대_코드_생성_응답 = 가족_초대_코드_생성_요청(accessToken);
            final String code = toObject(가족_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

            초대코드로_회원가입_요청(memberId2, code);

            // when
            final ExtractableResponse<Response> response = 마이_그룹별_조회_요청(accessToken);
            final List<GroupResponseWithFamily> groups = toObject(response, MyProfileResponse.class).groups();

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(groups).hasSize(1),
                    () -> assertThat(groups.get(0).members().stream().map(GroupMemberResponse::memberId).toList())
                            .containsExactly(memberId1, memberId2)
            );
        }

        @Test
        void 가족_그룹과_다른_그룹의_멤버들을_조회한다() {
            // given
            final String memberId1 = "memberId1";
            final String memberId2 = "memberId2";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);
            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

            외가_그룹_추가_요청(accessToken);

            final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
            final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

            초대코드로_회원가입_요청(memberId2, code);

            // when
            final ExtractableResponse<Response> response = 마이_그룹별_조회_요청(accessToken);
            final List<GroupResponseWithFamily> groups = toObject(response, MyProfileResponse.class).groups();

            final GroupResponseWithFamily familyGroup = groups.stream()
                    .filter(GroupResponseWithFamily::family)
                    .findAny()
                    .orElseThrow();
            final GroupResponseWithFamily notFamilyGroup = groups.stream()
                    .filter(group -> !group.family())
                    .findAny()
                    .orElseThrow();

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(groups).hasSize(2),
                    () -> assertThat(familyGroup.members().stream().map(GroupMemberResponse::memberId).toList())
                            .containsExactly(memberId1),
                    () -> assertThat(notFamilyGroup.members().stream().map(GroupMemberResponse::memberId).toList())
                            .containsExactly(memberId2)
            );
        }
    }

    @Test
    void 그룹_정보_변경_요청_시_그룹명과_그룹_컬러가_변경된다() {
        // given
        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        외가_그룹_추가_요청(accessToken);

        // when
        final ExtractableResponse<Response> response = 그룹_정보_변경_요청(accessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 그룹_멤버_정보_변경_요청_시_그룹명이_변경된다() {
        // given
        final String invitedMemberId = "memberId";

        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        외가_그룹_추가_요청(accessToken);

        final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
        final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

        초대코드로_회원가입_요청(invitedMemberId, code);

        // when
        final ExtractableResponse<Response> response = 그룹_멤버_정보_변경_요청(accessToken, invitedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 그룹_멤버_삭제_시_아기와_멤버간의_관계가_삭제된다() {
        // given
        final String invitedMemberId = "memberId";

        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        외가_그룹_추가_요청(accessToken);

        final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
        final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

        초대코드로_회원가입_요청(invitedMemberId, code);

        final ExtractableResponse<Response> 마이_그룹별_조회_응답 = 마이_그룹별_조회_요청(accessToken);
        final List<GroupResponseWithFamily> groups = toObject(마이_그룹별_조회_응답, MyProfileResponse.class).groups();

        final GroupResponseWithFamily notFamilyGroup = groups.stream()
                .filter(group -> !group.family())
                .findAny()
                .orElseThrow();

        assertThat(notFamilyGroup.members()).isNotEmpty();

        // when
        final ExtractableResponse<Response> response = 그룹_멤버_삭제_요청(accessToken, invitedMemberId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final ExtractableResponse<Response> 마이_그룹별_조회_응답2 = 마이_그룹별_조회_요청(accessToken);
        final List<GroupResponseWithFamily> groups2 = toObject(마이_그룹별_조회_응답2, MyProfileResponse.class).groups();

        final GroupResponseWithFamily notFamilyGroup2 = groups2.stream()
                .filter(group -> !group.family())
                .findAny()
                .orElseThrow();

        assertThat(notFamilyGroup2.members()).isEmpty();
    }

    @Test
    void 그룹_삭제_시_그룹_및_아기와_그룹_멤버간의_관계가_삭제된다() {
        // given
        final String memberId = "memberId";

        final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(멤버_가입_요청_데이터);
        final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
        외가_그룹_추가_요청(accessToken);

        final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
        final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

        초대코드로_회원가입_요청(memberId, code);

        final ExtractableResponse<Response> 마이_그룹별_조회_응답 = 마이_그룹별_조회_요청(accessToken);
        final List<GroupResponseWithFamily> groups = toObject(마이_그룹별_조회_응답, MyProfileResponse.class).groups();

        assertThat(groups).hasSize(2);

        // when
        final ExtractableResponse<Response> response = 외가_그룹_삭제_요청(accessToken);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final ExtractableResponse<Response> 마이_그룹별_조회_응답2 = 마이_그룹별_조회_요청(accessToken);
        final List<GroupResponseWithFamily> groups2 = toObject(마이_그룹별_조회_응답2, MyProfileResponse.class).groups();

        assertThat(groups2).hasSize(1);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Picker<Color> picker() {
            return colors -> Color.COLOR_1;
        }
    }

    @Nested
    class 다른_아기_프로필_조회_요청_시_ {

        @Test
        void 자신의_아기라면_가족_그룹의_정보를_조회한다() {
            // given
            final String memberId1 = "memberId";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);

            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();
            final String babyId = getBabyId(아기_등록_회원가입_응답);

            // when
            final ExtractableResponse<Response> response = 다른_아기_프로필_조회_요청(accessToken, babyId);
            final BabyProfileResponse babyProfileResponse = toObject(response, BabyProfileResponse.class);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(babyProfileResponse.familyGroup().members()).hasSize(1),
                    () -> assertThat(babyProfileResponse.familyGroup().babies()).hasSize(2),
                    () -> assertThat(babyProfileResponse.myGroup()).isNull()
            );
        }

        @Test
        void 자신의_아기가_아니라면_가족_그룹의_정보와_자신의_소속_그룹의_정보를_조회한다() {
            // given
            final String memberId1 = "memberId1";
            final String memberId2 = "memberId2";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);
            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

            외가_그룹_추가_요청(accessToken);

            final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
            final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

            final ExtractableResponse<Response> 초대코드로_회원가입_응답 = 초대코드로_회원가입_요청(memberId2, code);
            final String invitedMemberAccessToken = toObject(초대코드로_회원가입_응답, MemberSignUpResponse.class).accessToken();
            final String babyId = getBabyId(초대코드로_회원가입_응답);

            // when
            final ExtractableResponse<Response> response = 다른_아기_프로필_조회_요청(invitedMemberAccessToken, babyId);
            final BabyProfileResponse babyProfileResponse = toObject(response, BabyProfileResponse.class);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(babyProfileResponse.familyGroup().members()).hasSize(1),
                    () -> assertThat(babyProfileResponse.familyGroup().babies()).hasSize(2),
                    () -> assertThat(babyProfileResponse.myGroup().members()).hasSize(1)
            );
        }

        @Test
        void 자신의_아기를_추가해도_조회하는_아기가_자신의_아기는_아니라면_가족_그룹의_정보와_자신의_소속_그룹의_정보를_조회한다() {
            // given
            final String memberId1 = "memberId1";
            final String memberId2 = "memberId2";

            final ExtractableResponse<Response> 아기_등록_회원가입_응답 = 아기_등록_회원가입_요청(memberId1);
            final String accessToken = toObject(아기_등록_회원가입_응답, MemberSignUpResponse.class).accessToken();

            외가_그룹_추가_요청(accessToken);

            final ExtractableResponse<Response> 외가_초대_코드_생성_응답 = 외가_초대_코드_생성_요청(accessToken);
            final String code = toObject(외가_초대_코드_생성_응답, CreateInviteCodeResponse.class).inviteCode();

            final ExtractableResponse<Response> 초대코드로_회원가입_응답 = 초대코드로_회원가입_요청(memberId2, code);
            final String invitedMemberAccessToken = toObject(초대코드로_회원가입_응답, MemberSignUpResponse.class).accessToken();

            final ExtractableResponse<Response> 아기_추가_응답 = 아기_추가_요청(invitedMemberAccessToken);
            final String invitedMemberBabyId = getBabyId(아기_추가_응답);

            // when
            final ExtractableResponse<Response> response = 다른_아기_프로필_조회_요청(invitedMemberAccessToken,
                    invitedMemberBabyId);
            final BabyProfileResponse babyProfileResponse = toObject(response, BabyProfileResponse.class);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(babyProfileResponse.familyGroup().members()).hasSize(1),
                    () -> assertThat(babyProfileResponse.familyGroup().babies()).hasSize(1),
                    () -> assertThat(babyProfileResponse.myGroup()).isNull()
            );
        }
    }
}
