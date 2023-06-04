package com.baba.back.baby.service;

import static com.baba.back.fixture.DomainFixture.nowDate;
import static com.baba.back.fixture.DomainFixture.관계10;
import static com.baba.back.fixture.DomainFixture.관계11;
import static com.baba.back.fixture.DomainFixture.관계12;
import static com.baba.back.fixture.DomainFixture.관계20;
import static com.baba.back.fixture.DomainFixture.관계21;
import static com.baba.back.fixture.DomainFixture.관계30;
import static com.baba.back.fixture.DomainFixture.관계40;
import static com.baba.back.fixture.DomainFixture.관계그룹10;
import static com.baba.back.fixture.DomainFixture.관계그룹11;
import static com.baba.back.fixture.DomainFixture.관계그룹20;
import static com.baba.back.fixture.DomainFixture.관계그룹21;
import static com.baba.back.fixture.DomainFixture.관계그룹30;
import static com.baba.back.fixture.DomainFixture.관계그룹40;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.아기3;
import static com.baba.back.fixture.DomainFixture.아기4;
import static com.baba.back.fixture.DomainFixture.초대10;
import static com.baba.back.fixture.DomainFixture.초대20;
import static com.baba.back.fixture.DomainFixture.초대코드정보1;
import static com.baba.back.fixture.RequestFixture.아기_이름_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.아기_추가_요청_데이터;
import static com.baba.back.fixture.RequestFixture.초대코드_생성_요청_데이터1;
import static com.baba.back.fixture.RequestFixture.초대코드로_아기_추가_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.CreateBabyRequest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeBabyResponse;
import com.baba.back.baby.dto.IsMyBabyResponse;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.baby.exception.BabyBadRequestException;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationsBadRequestException;
import com.baba.back.baby.exception.RelationBadRequestException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.exception.MemberAuthorizationException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
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
    private IdConstructor idConstructor;

    @Mock
    private Picker<Color> picker;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private RelationGroupRepository relationGroupRepository;

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
                        new IsMyBabyResponse(아기1.getId(), 관계그룹10.getGroupColor(), 아기1.getName(), true),
                        new IsMyBabyResponse(아기2.getId(), 관계그룹20.getGroupColor(), 아기2.getName(), true)
                ),
                () -> assertThat(babies.others()).containsExactly(
                        new IsMyBabyResponse(아기3.getId(), 관계그룹30.getGroupColor(), 아기3.getName(), false),
                        new IsMyBabyResponse(아기4.getId(), 관계그룹40.getGroupColor(), 아기4.getName(), false)
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
    void 초대코드_생성_요청시_소속그룹과_관계명이_동일한_초대코드가_이미_존재하면_초대코드를_업데이트_한다() {
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
        given(invitationRepository.findByRelationGroupAndRelationName(any(RelationGroup.class),
                eq(초대코드_생성_요청_데이터1.getRelationName()))).willReturn(Optional.of(초대10));
        given(invitationRepository.save(any(Invitation.class))).willReturn(any());

        // when
        final CreateInviteCodeResponse response = babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId());

        // then
        assertThat(response.inviteCode()).isEqualTo(inviteCode);
    }

    @Test
    void 초대코드_생성_요청시_소속그룹과_관계명이_동일한_초대코드가_없으면_초대_코드를_생성한다() {
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
        given(invitationRepository.findByRelationGroupAndRelationName(any(RelationGroup.class),
                eq(초대코드_생성_요청_데이터1.getRelationName()))).willReturn(Optional.empty());
        given(invitationRepository.save(any(Invitation.class))).willReturn(any());

        // when
        final CreateInviteCodeResponse response = babyService.createInviteCode(초대코드_생성_요청_데이터1, 멤버1.getId());

        // then
        assertThat(response.inviteCode()).isEqualTo(inviteCode);
    }

    @Test
    void 초대장_조회_요청시_등록된_초대가_없으면_예외를_던진다() {
        // given
        given(invitationRepository.findAllByCode(초대코드정보1.getCode())).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> babyService.searchInviteCode(초대코드정보1.getCode()))
                .isInstanceOf(InvitationsBadRequestException.class);
    }

    @Test
    void 초대장_조회_요청시_초대코드가_만료되었으면_예외를_던진다() {
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

        final Invitation invitation = Invitation.builder()
                .invitationCode(invitationCode)
                .relationGroup(관계그룹11)
                .build();

        given(invitationRepository.findAllByCode(validInviteCode)).willReturn(List.of(invitation));

        final Clock timeTravelClock = Clock.fixed(now.plusDays(10).plusSeconds(1)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        given(clock.instant()).willReturn(timeTravelClock.instant());
        given(clock.getZone()).willReturn(timeTravelClock.getZone());

        // when & then
        assertThatThrownBy(() -> babyService.searchInviteCode(validInviteCode))
                .isInstanceOf(InvitationCodeBadRequestException.class);
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

        given(invitationRepository.findAllByCode(validInviteCode)).willReturn(List.of(초대10, 초대20));

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

    @Nested
    class 아기_추가_요청_시_ {

        final String memberId = 멤버1.getId();
        final Clock now = Clock.systemDefaultZone();

        @Test
        void 자신의_아기가_없다면_아기를_추가한다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(relationRepository.findAllByMemberAndRelationGroupFamily(any(Member.class), eq(true)))
                    .willReturn(List.of());

            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());
            given(idConstructor.createId()).willReturn(아기1.getId());
            given(babyRepository.save(any(Baby.class))).willReturn(아기1);

            given(picker.pick(anyList())).willReturn(Color.COLOR_1);

            // when
            final String babyId = babyService.createBaby(memberId, 아기_추가_요청_데이터);

            // then
            assertThat(babyId).isEqualTo(아기1.getId());

            then(babyRepository).should(times(1)).save(any(Baby.class));
            then(relationGroupRepository).should(times(1)).save(any(RelationGroup.class));
            then(relationRepository).should(times(1)).save(any(Relation.class));
        }

        @Test
        void 동일한_이름의_아기가_존재하면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());
            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());
            given(idConstructor.createId()).willReturn(아기1.getId());
            given(babyRepository.save(any(Baby.class))).willReturn(아기1);

            given(relationRepository.findAllByMemberAndRelationGroupFamily(any(Member.class), eq(true)))
                    .willReturn(List.of(관계10, 관계20));

            final CreateBabyRequest request = new CreateBabyRequest("아기1", "아빠", LocalDate.now());

            // when & then
            assertThatThrownBy(() -> babyService.createBaby(memberId, request))
                    .isInstanceOf(BabyBadRequestException.class);
        }

        @Test
        void 자신의_아기가_있어도_아기를_추가할_수_있다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(relationRepository.findAllByMemberAndRelationGroupFamily(any(Member.class), eq(true)))
                    .willReturn(List.of(관계10, 관계20));
            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());
            given(idConstructor.createId()).willReturn(아기1.getId());
            given(babyRepository.save(any(Baby.class))).willReturn(아기1);

            given(relationGroupRepository.findAllByBaby(any(Baby.class))).willReturn(List.of(관계그룹10, 관계그룹11));
            given(relationRepository.findAllByRelationGroupIn(anyList())).willReturn(List.of(관계10, 관계11, 관계12));

            // when
            final String babyId = babyService.createBaby(memberId, 아기_추가_요청_데이터);

            // then
            assertThat(babyId).isEqualTo(아기1.getId());

            then(babyRepository).should(times(1)).save(any(Baby.class));
            then(relationGroupRepository).should(times(3)).save(any(RelationGroup.class));
            then(relationRepository).should(times(3)).save(any(Relation.class));
        }
    }

    @Nested
    class 아기_이름_변경_요청_시_ {
        final String memberId = 멤버1.getId();
        final String babyId = 아기1.getId();
        final String babyName = 아기_이름_변경_요청_데이터.getName();

        @Test
        void 아기가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> babyService.updateBabyName(memberId, babyId, babyName))
                    .isInstanceOf(BabyNotFoundException.class);
        }

        @Test
        void 아기와의_관계가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> babyService.updateBabyName(memberId, babyId, babyName))
                    .isInstanceOf(RelationNotFoundException.class);
        }

        @Test
        void 아기와_가족_관계가_아니면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계12));

            // when & then
            assertThatThrownBy(() -> babyService.updateBabyName(memberId, babyId, babyName))
                    .isInstanceOf(MemberAuthorizationException.class);
        }

        @Test
        void 아기의_이름을_변경한다() {
            // given
            final Baby baby = Baby.builder()
                    .id("baby1")
                    .name("아기1")
                    .birthday(nowDate)
                    .now(nowDate)
                    .build();

            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(baby));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계10));

            // when
            babyService.updateBabyName(memberId, babyId, babyName);

            // then
            then(babyRepository).should(times(1)).save(any(Baby.class));
        }
    }

    @Nested
    class 초대코드로_아기_추가_요청_시_ {

        final String memberId = 멤버1.getId();
        final String inviteCode = 초대코드로_아기_추가_요청_데이터.getInviteCode();

        @Test
        void 생성된_초대코드가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(invitationRepository.findAllByCode(inviteCode)).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> babyService.addBabyWithCode(초대코드로_아기_추가_요청_데이터, memberId))
                    .isInstanceOf(InvitationsBadRequestException.class);
        }

        @Test
        void 만료된_초대코드라면_예외를_던진다() {
            // given
            final LocalDateTime now = LocalDateTime.now();

            final Clock nowClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
            given(clock.instant()).willReturn(nowClock.instant());
            given(clock.getZone()).willReturn(nowClock.getZone());

            final InvitationCode invitationCode = InvitationCode.builder()
                    .code(Code.from((length, chars) -> inviteCode))
                    .relationName("이모")
                    .now(LocalDateTime.now(clock))
                    .build();

            final Invitation invitation = Invitation.builder()
                    .invitationCode(invitationCode)
                    .relationGroup(관계그룹11)
                    .build();

            final Clock timeTravelClock = Clock.fixed(now.plusDays(10).plusSeconds(1)
                    .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(invitationRepository.findAllByCode(inviteCode)).willReturn(List.of(invitation));

            given(clock.instant()).willReturn(timeTravelClock.instant());
            given(clock.getZone()).willReturn(timeTravelClock.getZone());

            // when & then
            assertThatThrownBy(() -> babyService.addBabyWithCode(초대코드로_아기_추가_요청_데이터, memberId))
                    .isInstanceOf(InvitationCodeBadRequestException.class);
        }

        @Test
        void 아기와의_관계가_이미_존재하면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(invitationRepository.findAllByCode(inviteCode)).willReturn(List.of(초대10, 초대20));

            final Clock now = Clock.systemDefaultZone();
            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());

            given(relationRepository.findAllByMember(any(Member.class))).willReturn(List.of(관계12, 관계21));

            // when & then
            assertThatThrownBy(() -> babyService.addBabyWithCode(초대코드로_아기_추가_요청_데이터, memberId))
                    .isInstanceOf(RelationBadRequestException.class);
        }

        @Test
        void 관계를_생성한다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(invitationRepository.findAllByCode(inviteCode)).willReturn(List.of(초대10, 초대20));

            final Clock now = Clock.systemDefaultZone();
            given(clock.instant()).willReturn(now.instant());
            given(clock.getZone()).willReturn(now.getZone());

            given(relationRepository.findAllByMember(any(Member.class))).willReturn(List.of());

            // when
            babyService.addBabyWithCode(초대코드로_아기_추가_요청_데이터, memberId);

            // then
            then(relationRepository).should(times(2)).save(any(Relation.class));
        }
    }
}
