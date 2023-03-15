package com.baba.back.invitation.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.관계그룹5;
import static com.baba.back.fixture.DomainFixture.관계그룹6;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.초대코드정보;
import static com.baba.back.fixture.DomainFixture.초대1;
import static com.baba.back.fixture.DomainFixture.초대2;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.baba.back.baby.domain.Baby;
import com.baba.back.invitation.domain.InvitationCode;
import com.baba.back.invitation.domain.Invitation;
import com.baba.back.invitation.dto.CreateInviteCodeResponse;
import com.baba.back.invitation.exception.RelationGroupNotFoundException;
import com.baba.back.invitation.repository.InvitationRepository;
import com.baba.back.invitation.repository.InvitationCodeRepository;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @InjectMocks
    private InvitationService invitationService;

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
    private Clock clock;

    @Test
    void 초대코드_생성_요청시_멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> invitationService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_멤버가_자신의_아이가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> invitationService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_그룹명이_존재하지_않으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계1, 관계2));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(
                any(Baby.class), eq(초대코드_생성_요청_데이터1.getRelationGroup())))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> invitationService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId()))
                .isInstanceOf(RelationGroupNotFoundException.class);
    }

    @Test
    void 초대코드_생성_요청시_초대_코드를_생성한다() {
        // given
        final Clock now = Clock.systemDefaultZone();

        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계1, 관계2));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기1, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹5));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기2, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹6));
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(invitationCodeRepository.save(any(InvitationCode.class))).willReturn(초대코드정보);
        given(invitationRepository.save(any(Invitation.class))).willReturn(초대1, 초대2);

        // when
        final CreateInviteCodeResponse response = invitationService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId());

        // then
        assertThat(response.inviteCode()).isNotBlank();
    }
}