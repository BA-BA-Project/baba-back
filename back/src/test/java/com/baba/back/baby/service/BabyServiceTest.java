package com.baba.back.baby.service;

import static com.baba.back.fixture.DomainFixture.관계10;
import static com.baba.back.fixture.DomainFixture.관계20;
import static com.baba.back.fixture.DomainFixture.관계30;
import static com.baba.back.fixture.DomainFixture.관계40;
import static com.baba.back.fixture.DomainFixture.관계그룹10;
import static com.baba.back.fixture.DomainFixture.관계그룹20;
import static com.baba.back.fixture.DomainFixture.관계그룹30;
import static com.baba.back.fixture.DomainFixture.관계그룹40;
import static com.baba.back.fixture.DomainFixture.관계그룹11;
import static com.baba.back.fixture.DomainFixture.관계그룹21;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.아기3;
import static com.baba.back.fixture.DomainFixture.아기4;
import static com.baba.back.fixture.DomainFixture.초대10;
import static com.baba.back.fixture.DomainFixture.초대20;
import static com.baba.back.fixture.DomainFixture.초대코드정보;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeBabyResponse;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationCodeNotFoundException;
import com.baba.back.baby.exception.InvitationNotFoundException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.InvitationCodeRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BabyServiceTest {

    @InjectMocks
    private BabyService babyService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private RelationGroupRepository relationGroupRepository;

    @Mock
    private InvitationCodeRepository invitationCodeRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private Clock clock;

    @Test
    void 존재하지_않는_멤버가_등록된_아기_리스트를_조회할_때_예외를_던진다() {
        // given
        final String invalidMemberId = "invalidMemberId";
        given(memberRepository.findById(invalidMemberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.findBabies(invalidMemberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 등록된_아기_리스트를_조회하면_가족_그룹에_해당하는_아이들과_가족_그룹이_아닌_아이들이_반환된다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMember(멤버1)).willReturn(List.of(관계20, 관계40, 관계10, 관계30));

        // when
        final BabiesResponse babies = babyService.findBabies(멤버1.getId());

        // then
        assertAll(
                () -> assertThat(babies.myBaby()).containsExactly(
                        new BabyResponse(아기1.getId(), 관계그룹10.getGroupColor(), 아기1.getName()),
                        new BabyResponse(아기2.getId(), 관계그룹20.getGroupColor(), 아기2.getName())
                ),
                () -> assertThat(babies.others()).containsExactly(
                        new BabyResponse(아기3.getId(), 관계그룹30.getGroupColor(), 아기3.getName()),
                        new BabyResponse(아기4.getId(), 관계그룹40.getGroupColor(), 아기4.getName())
                )
        );
    }

    @Test
    void 초대코드_생성_요청시_멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_멤버가_자신의_아이가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_그룹명이_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계10, 관계20));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(
                any(Baby.class), eq(초대코드_생성_요청_데이터1.getRelationGroup())))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(RelationGroupNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_초대_코드를_생성한다() {
        // given
        final String inviteCode = "AAAAAA";
        final Clock now = Clock.systemDefaultZone();

        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계10, 관계20));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기1, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹11));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기2, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹21));
        given(codeGenerator.generate(anyInt(), anyString())).willReturn(inviteCode);
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(invitationCodeRepository.save(any(InvitationCode.class))).willReturn(초대코드정보);
        given(invitationRepository.save(any(Invitation.class))).willReturn(초대10, 초대20);

        // when
        final CreateInviteCodeResponse response = babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId());

        // then
        assertThat(response.inviteCode()).isEqualTo(inviteCode);
    }

    @Test
    void 초대코드_조회_요청시_해당_초대코드가_존재하지_않으면_예외를_던진다() {
        // given
        given(invitationCodeRepository.findByCodeValue(초대코드정보.getCode().getValue()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.searchInviteCode(초대코드정보.getCode().getValue()))
                .isInstanceOf(InvitationCodeNotFoundException.class);
    }

    @Test
    void 초대코드_조회_요청시_초대코드가_만료되었으면_예외를_던진다() {
        // given
        final String validInviteCode = "AAAAAA";
        final LocalDateTime now = LocalDateTime.now();

        final Clock nowClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(nowClock.instant());
        given(clock.getZone()).willReturn(nowClock.getZone());

        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> validInviteCode))
                .relationName("이모")
                .now(LocalDateTime.now(clock))
                .build();

        given(invitationCodeRepository.findByCodeValue(validInviteCode)).willReturn(Optional.of(invitationCode));

        final Clock timeTravelClock = Clock.fixed(now.plusDays(10).plusSeconds(1)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        given(clock.instant()).willReturn(timeTravelClock.instant());
        given(clock.getZone()).willReturn(timeTravelClock.getZone());

        // when & then
        assertThatThrownBy(
                () -> babyService.searchInviteCode(validInviteCode))
                .isInstanceOf(InvitationCodeBadRequestException.class);
    }

    @Test
    void 초대코드_조회_요청시_등록된_초대가_없으면_예외를_던진다() {
        // given
        final String validInviteCode = "AAAAAA";

        final Clock now = Clock.systemDefaultZone();
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());

        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> validInviteCode))
                .relationName("이모")
                .now(LocalDateTime.now(clock))
                .build();

        given(invitationCodeRepository.findByCodeValue(validInviteCode)).willReturn(Optional.of(invitationCode));
        given(invitationRepository.findAllByInvitationCode(invitationCode)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> babyService.searchInviteCode(validInviteCode))
                .isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    void 초대코드_조회_요청시_관련_정보를_확인할_수_있다() {
        // given
        final String validInviteCode = "AAAAAA";

        final Clock now = Clock.systemDefaultZone();
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());

        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> validInviteCode))
                .relationName("이모")
                .now(LocalDateTime.now(clock))
                .build();

        given(invitationCodeRepository.findByCodeValue(validInviteCode)).willReturn(Optional.of(invitationCode));
        given(invitationRepository.findAllByInvitationCode(invitationCode)).willReturn(List.of(초대10, 초대20));

        // when
        final SearchInviteCodeResponse response = babyService.searchInviteCode(validInviteCode);

        // then
        assertAll(
                () -> assertThat(response.relationName()).isEqualTo(invitationCode.getRelationName()),
                () -> assertThat(response.babies()).containsExactly(
                        new InviteCodeBabyResponse(아기1.getName()),
                        new InviteCodeBabyResponse(아기2.getName()))
        );
    }
}
