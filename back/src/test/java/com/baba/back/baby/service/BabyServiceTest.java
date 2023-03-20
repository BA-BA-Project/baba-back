package com.baba.back.baby.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.관계3;
import static com.baba.back.fixture.DomainFixture.관계4;
import static com.baba.back.fixture.DomainFixture.관계그룹1;
import static com.baba.back.fixture.DomainFixture.관계그룹2;
import static com.baba.back.fixture.DomainFixture.관계그룹3;
import static com.baba.back.fixture.DomainFixture.관계그룹4;
import static com.baba.back.fixture.DomainFixture.관계그룹5;
import static com.baba.back.fixture.DomainFixture.관계그룹6;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.아기3;
import static com.baba.back.fixture.DomainFixture.아기4;
import static com.baba.back.fixture.DomainFixture.초대1;
import static com.baba.back.fixture.DomainFixture.초대2;
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
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.InvitationCodeRepository;
import com.baba.back.baby.repository.InvitationRepository;
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
        given(relationRepository.findAllByMember(멤버1)).willReturn(List.of(관계2, 관계4, 관계1, 관계3));

        // when
        final BabiesResponse babies = babyService.findBabies(멤버1.getId());

        // then
        assertAll(
                () -> assertThat(babies.myBaby()).containsExactly(
                        new BabyResponse(아기1.getId(), 관계그룹1.getGroupColor(), 아기1.getName()),
                        new BabyResponse(아기2.getId(), 관계그룹2.getGroupColor(), 아기2.getName())
                ),
                () -> assertThat(babies.others()).containsExactly(
                        new BabyResponse(아기3.getId(), 관계그룹3.getGroupColor(), 아기3.getName()),
                        new BabyResponse(아기4.getId(), 관계그룹4.getGroupColor(), 아기4.getName())
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
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계1, 관계2));
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
        given(relationRepository.findAllByMemberAndRelationGroupFamily(멤버1, true)).willReturn(List.of(관계1, 관계2));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기1, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹5));
        given(relationGroupRepository.findByBabyAndRelationGroupNameValue(아기2, 초대코드_생성_요청_데이터1.getRelationGroup()))
                .willReturn(Optional.of(관계그룹6));
        given(codeGenerator.generate(anyInt(), anyString())).willReturn(inviteCode);
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(invitationCodeRepository.save(any(InvitationCode.class))).willReturn(초대코드정보);
        given(invitationRepository.save(any(Invitation.class))).willReturn(초대1, 초대2);

        // when
        final CreateInviteCodeResponse response = babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId());

        // then
        assertThat(response.inviteCode()).isEqualTo(inviteCode);
    }
}
