package com.baba.back.oauth.service;

import static com.baba.back.fixture.DomainFixture.관계10;
import static com.baba.back.fixture.DomainFixture.관계11;
import static com.baba.back.fixture.DomainFixture.관계12;
import static com.baba.back.fixture.DomainFixture.관계20;
import static com.baba.back.fixture.DomainFixture.관계22;
import static com.baba.back.fixture.DomainFixture.관계30;
import static com.baba.back.fixture.DomainFixture.관계그룹10;
import static com.baba.back.fixture.DomainFixture.관계그룹11;
import static com.baba.back.fixture.DomainFixture.관계그룹20;
import static com.baba.back.fixture.DomainFixture.관계그룹21;
import static com.baba.back.fixture.DomainFixture.관계그룹30;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.멤버2;
import static com.baba.back.fixture.DomainFixture.멤버3;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.초대10;
import static com.baba.back.fixture.DomainFixture.초대20;
import static com.baba.back.fixture.RequestFixture.그룹_추가_요청_데이터1;
import static com.baba.back.fixture.RequestFixture.그룹_추가_요청_데이터2;
import static com.baba.back.fixture.RequestFixture.마이_프로필_변경_요청_데이터;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청_데이터;
import static com.baba.back.fixture.RequestFixture.초대코드로_멤버_가입_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.domain.invitation.Code;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.baby.exception.BabyNotFoundException;
import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationsBadRequestException;
import com.baba.back.baby.exception.RelationGroupNotFoundException;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.baby.repository.InvitationRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.Color;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.BabyProfileResponse;
import com.baba.back.oauth.dto.CreateGroupRequest;
import com.baba.back.oauth.dto.FamilyGroupResponse;
import com.baba.back.oauth.dto.GroupMemberResponse;
import com.baba.back.oauth.dto.GroupResponse;
import com.baba.back.oauth.dto.GroupResponseWithFamily;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.MyProfileResponse;
import com.baba.back.oauth.dto.SignUpWithBabyResponse;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationGroupBadRequestException;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationGroupRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationGroupRepository relationGroupRepository;

    @Mock
    private RelationRepository relationRepository;

    @Spy
    private Picker<Color> picker;

    @Mock
    private Clock clock;

    @Mock
    private IdConstructor idConstructor;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    void 이미_회원가입한_멤버는_회원가입할_수_없다() {
        // given
        final String memberId = "memberId";

        given(memberRepository.existsById(memberId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signUpWithBaby(new MemberSignUpRequest(), memberId))
                .isInstanceOf(MemberBadRequestException.class);
    }

    @Test
    void 회원가입을_진행한다() {
        // given
        final String memberId = "memberId";
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";
        final Clock now = Clock.systemDefaultZone();

        given(memberRepository.existsById(memberId)).willReturn(false);
        given(picker.pick(anyList())).willReturn(Color.COLOR_1);
        given(memberRepository.save(any(Member.class))).willReturn(멤버1);
        given(idConstructor.createId()).willReturn(아기1.getId(), 아기2.getId());
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(babyRepository.save(any(Baby.class))).willReturn(아기1, 아기2);
        given(relationGroupRepository.save(any(RelationGroup.class))).willReturn(관계그룹10, 관계그룹30);
        given(relationRepository.save(any(Relation.class))).willReturn(관계10, 관계30);
        given(accessTokenProvider.createToken(memberId)).willReturn(accessToken);
        given(refreshTokenProvider.createToken(memberId)).willReturn(refreshToken);
        given(tokenRepository.save(any(Token.class))).willReturn(any());

        // when
        final SignUpWithBabyResponse response = memberService.signUpWithBaby(멤버_가입_요청_데이터, memberId);

        //then
        then(memberRepository).should(times(1)).save(any());
        then(idConstructor).should(times(2)).createId();
        then(babyRepository).should(times(2)).save(any());
        then(relationGroupRepository).should(times(2)).save(any(RelationGroup.class));
        then(relationRepository).should(times(2)).save(any());

        final MemberSignUpResponse memberSignUpResponse = response.memberSignUpResponse();
        assertAll(
                () -> assertThat(memberSignUpResponse.accessToken()).isEqualTo(accessToken),
                () -> assertThat(memberSignUpResponse.refreshToken()).isEqualTo(refreshToken)
        );
    }

    @Test
    void 멤버의_정보를_조회한다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));

        // when
        final MemberResponse response = memberService.findMember(멤버1.getId());

        // then
        assertThat(response).isEqualTo(
                new MemberResponse(
                        멤버1.getId(),
                        멤버1.getName(),
                        멤버1.getIntroduction(),
                        멤버1.getIconName(),
                        멤버1.getIconColor()
                )
        );
    }

    @Test
    void 존재하지_않는_멤버의_정보를_조회할_수_없다() {
        final String invalidMemberId = "invalidMemberId";

        // given
        given(memberRepository.findById(invalidMemberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findMember(invalidMemberId)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 초대코드로_회원가입시_이미_회원가입한_멤버는_회원가입할_수_없다() {
        // given
        final String memberId = "memberId";
        given(memberRepository.existsById(memberId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signUpWithCode(초대코드로_멤버_가입_요청_데이터, memberId))
                .isInstanceOf(MemberBadRequestException.class);
    }

    @Test
    void 초대코드로_회원가입시_요청한_초대코드에_해당하는_초대정보가_없으면_예외를_던진다() {
        // given
        final String memberId = "memberId";
        given(memberRepository.existsById(memberId)).willReturn(false);
        given(invitationRepository.findAllByCode(초대코드로_멤버_가입_요청_데이터.getInviteCode())).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> memberService.signUpWithCode(초대코드로_멤버_가입_요청_데이터, memberId))
                .isInstanceOf(InvitationsBadRequestException.class);
    }

    @Test
    void 초대코드로_회원가입시_초대코드가_만료되었으면_예외를_던진다() {
        // given
        final String memberId = "memberId";
        final String validCode = "AAAAAA";
        final LocalDateTime now = LocalDateTime.now();
        final SignUpWithCodeRequest request = new SignUpWithCodeRequest(validCode, "박재희", "PROFILE_W_1");

        final Clock nowClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(nowClock.instant());
        given(clock.getZone()).willReturn(nowClock.getZone());

        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> validCode))
                .relationName("이모")
                .now(LocalDateTime.now(clock))
                .build();

        final Invitation invitation = Invitation.builder()
                .invitationCode(invitationCode)
                .relationGroup(관계그룹11)
                .build();

        final Clock timeTravelClock = Clock.fixed(now.plusDays(10).plusSeconds(1)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        given(memberRepository.existsById(memberId)).willReturn(false);
        given(invitationRepository.findAllByCode(validCode)).willReturn(List.of(invitation));

        given(clock.instant()).willReturn(timeTravelClock.instant());
        given(clock.getZone()).willReturn(timeTravelClock.getZone());

        // when & then
        assertThatThrownBy(() -> memberService.signUpWithCode(request, memberId))
                .isInstanceOf(InvitationCodeBadRequestException.class);
    }

    @Test
    void 초대코드로_회원가입시_회원가입을_진행한다() {
        // given
        final String memberId = "memberId";
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        given(memberRepository.existsById(memberId)).willReturn(false);
        given(invitationRepository.findAllByCode(초대코드로_멤버_가입_요청_데이터.getInviteCode()))
                .willReturn(List.of(초대10, 초대20));

        final Clock now = Clock.systemDefaultZone();
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(memberRepository.save(any(Member.class))).willReturn(멤버3);
        given(relationRepository.save(any(Relation.class))).willReturn(관계12, 관계22);
        given(accessTokenProvider.createToken(memberId)).willReturn(accessToken);
        given(refreshTokenProvider.createToken(memberId)).willReturn(refreshToken);
        given(tokenRepository.save(any(Token.class))).willReturn(any());

        // when
        final MemberSignUpResponse response = memberService.signUpWithCode(초대코드로_멤버_가입_요청_데이터, memberId)
                .memberSignUpResponse();

        // then
        assertAll(
                () -> assertThat(response.accessToken()).isEqualTo(accessToken),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );
    }

    @Test
    void 그룹_추가시_멤버가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.createGroup(멤버1.getId(), 그룹_추가_요청_데이터1))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 그룹_추가시_자신의_아기가_없으면_예외를_던진다() {
        // given
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMember(멤버1)).willReturn(List.of(관계22));

        // when & then
        assertThatThrownBy(() -> memberService.createGroup(멤버1.getId(), 그룹_추가_요청_데이터1))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 그룹_추가시_같은_이름의_그룹이_이미_존재하면_예외를_던진다() {
        // given
        final String memberId = 멤버1.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMember(멤버1)).willReturn(List.of(관계10, 관계20, 관계22));
        given(relationGroupRepository.findAllByBabyIn(anyList())).willReturn(List.of(관계그룹10, 관계그룹11, 관계그룹20, 관계그룹21));

        final CreateGroupRequest invalidRequest = new CreateGroupRequest("외가", "FFAEBA");

        // when & then
        assertThatThrownBy(() -> memberService.createGroup(memberId, invalidRequest))
                .isInstanceOf(RelationGroupBadRequestException.class);
    }

    @Test
    void 그룹_추가시_자신의_아기들의_관계그룹을_각각_생성한다() {
        // given
        final String memberId = 멤버1.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
        given(relationRepository.findAllByMember(멤버1)).willReturn(List.of(관계10, 관계20, 관계22));
        given(relationGroupRepository.findAllByBabyIn(anyList())).willReturn(List.of(관계그룹10, 관계그룹11, 관계그룹20, 관계그룹21));

        // when
        memberService.createGroup(memberId, 그룹_추가_요청_데이터2);

        // when & then
        then(relationGroupRepository).should(times(2)).save(any(RelationGroup.class));
    }

    @Test
    void 마이_프로필_조회시_멤버가_없으면_예외를_던진다() {
        // given
        final String memberId = 멤버1.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.searchMyGroups(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 마이_프로필_조회시_자신의_아기가_없으면_예외를_던진다() {
        // given
        final String memberId = 멤버1.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
        given(relationRepository.findFirstByMemberAndRelationGroupFamily(멤버1, true)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.searchMyGroups(memberId))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 마이_프로필_조회시_그룹별_멤버들을_조회할_수_있다() {
        // given
        final String memberId = 멤버1.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
        given(relationRepository.findFirstByMemberAndRelationGroupFamily(멤버1, true))
                .willReturn(Optional.of(관계10));
        given(relationGroupRepository.findAllByBaby(any(Baby.class))).willReturn(List.of(관계그룹10, 관계그룹11));
        given(relationRepository.findAllByRelationGroupIn(anyList())).willReturn(List.of(관계10, 관계11, 관계12));

        // when
        final MyProfileResponse response = memberService.searchMyGroups(memberId);

        // then
        assertThat(response.groups()).contains(
                new GroupResponseWithFamily(관계그룹10.getRelationGroupName(), 관계그룹10.isFamily(),
                        List.of(new GroupMemberResponse(
                                        멤버1.getId(),
                                        멤버1.getName(),
                                        관계10.getRelationName(),
                                        멤버1.getIconName(),
                                        멤버1.getIconColor()
                                ),
                                new GroupMemberResponse(
                                        멤버2.getId(),
                                        멤버2.getName(),
                                        관계11.getRelationName(),
                                        멤버2.getIconName(),
                                        멤버2.getIconColor()
                                )
                        )
                ),
                new GroupResponseWithFamily(관계그룹11.getRelationGroupName(), 관계그룹11.isFamily(),
                        List.of(new GroupMemberResponse(
                                        멤버3.getId(),
                                        멤버3.getName(),
                                        관계12.getRelationName(),
                                        멤버3.getIconName(),
                                        멤버3.getIconColor()
                                )
                        )
                )
        );
    }

    @Nested
    class 마이_프로필_변경_시_ {
        
        @Test
        void 멤버의_정보를_변경한다() {
            // given
            given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
            
            // when
            memberService.updateMember(멤버1.getId(), 마이_프로필_변경_요청_데이터);
            
            // then
            then(memberRepository).should().save(any(Member.class));
        }
    }

    @Nested
    class 다른_아기_프로필_조회_시 {

        final String memberId = 멤버1.getId();
        final String babyId = 아기1.getId();

        @Test
        void 멤버가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.searchBabyGroups(memberId, babyId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        void 아기가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.searchBabyGroups(memberId, babyId))
                    .isInstanceOf(BabyNotFoundException.class);
        }

        @Test
        void 멤버가_아기와_관계가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.searchBabyGroups(memberId, babyId))
                    .isInstanceOf(RelationNotFoundException.class);
        }

        @Test
        void 아기의_가족그룹이_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계10));
            given(relationGroupRepository.findByBabyAndFamily(any(Baby.class), eq(true)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.searchBabyGroups(memberId, babyId))
                    .isInstanceOf(RelationGroupNotFoundException.class);
        }

        @Test
        void 아기와_가족_관계인_멤버가_없으면_예외를_던진다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계10));
            given(relationGroupRepository.findByBabyAndFamily(any(Baby.class), eq(true)))
                    .willReturn(Optional.of(관계그룹10));

            given(relationRepository.findFirstByRelationGroup(any(RelationGroup.class))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.searchBabyGroups(memberId, babyId))
                    .isInstanceOf(RelationNotFoundException.class);
        }

        @Test
        void 자신의_아기를_조회했다면_가족_그룹의_정보만_조회할_수_있다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버1));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계10));
            given(relationGroupRepository.findByBabyAndFamily(any(Baby.class), eq(true)))
                    .willReturn(Optional.of(관계그룹10));

            given(relationRepository.findFirstByRelationGroup(any(RelationGroup.class))).willReturn(Optional.of(관계10));
            given(relationRepository.findAllByMember(any(Member.class))).willReturn(List.of(관계10, 관계20));
            given(relationRepository.findAllByRelationGroup(any(RelationGroup.class))).willReturn(List.of(관계10, 관계11));

            // when
            final BabyProfileResponse response = memberService.searchBabyGroups(memberId, babyId);
            final FamilyGroupResponse familyGroupResponse = response.familyGroup();

            // then
            assertAll(
                    () -> assertThat(familyGroupResponse.groupName()).isEqualTo(관계그룹10.getRelationGroupName()),
                    () -> assertThat(familyGroupResponse.babies()).containsExactly(
                            new BabyResponse(
                                    아기1.getId(),
                                    관계그룹10.getGroupColor(),
                                    아기1.getName()
                            ),
                            new BabyResponse(
                                    아기2.getId(),
                                    관계그룹20.getGroupColor(),
                                    아기2.getName()
                            )
                    ),
                    () -> assertThat(familyGroupResponse.members()).containsExactly(
                            new GroupMemberResponse(
                                    멤버1.getId(),
                                    멤버1.getName(),
                                    관계10.getRelationName(),
                                    멤버1.getIconName(),
                                    멤버1.getIconColor()
                            ),
                            new GroupMemberResponse(
                                    멤버2.getId(),
                                    멤버2.getName(),
                                    관계11.getRelationName(),
                                    멤버2.getIconName(),
                                    멤버2.getIconColor()
                            )
                    ),
                    () -> assertThat(response.myGroup()).isNull()
            );
        }


        @Test
        void 다른_사람의_아기를_조회했다면_가족_그룹의_아기들과_멤버들_그리고_자신의_소속_그룹의_멤버들을_조회할_수_있다() {
            // given
            given(memberRepository.findById(memberId)).willReturn(Optional.of(멤버3));
            given(babyRepository.findById(babyId)).willReturn(Optional.of(아기1));
            given(relationRepository.findByMemberAndBaby(any(Member.class), any(Baby.class)))
                    .willReturn(Optional.of(관계12));
            given(relationGroupRepository.findByBabyAndFamily(any(Baby.class), eq(true)))
                    .willReturn(Optional.of(관계그룹10));

            given(relationRepository.findFirstByRelationGroup(any(RelationGroup.class))).willReturn(
                    Optional.of(관계10));
            given(relationRepository.findAllByMember(any(Member.class))).willReturn(List.of(관계10, 관계20));
            given(relationRepository.findAllByRelationGroup(any(RelationGroup.class))).willReturn(
                    List.of(관계10, 관계11), List.of(관계12));

            // when
            final BabyProfileResponse response = memberService.searchBabyGroups(memberId, babyId);
            final FamilyGroupResponse familyGroupResponse = response.familyGroup();
            final GroupResponse myGroupResponse = response.myGroup();

            // then
            assertAll(
                    () -> assertThat(familyGroupResponse.groupName()).isEqualTo(관계그룹10.getRelationGroupName()),
                    () -> assertThat(familyGroupResponse.babies()).containsExactly(
                            new BabyResponse(
                                    아기1.getId(),
                                    관계그룹10.getGroupColor(),
                                    아기1.getName()
                            ),
                            new BabyResponse(
                                    아기2.getId(),
                                    관계그룹20.getGroupColor(),
                                    아기2.getName()
                            )
                    ),
                    () -> assertThat(familyGroupResponse.members()).containsExactly(
                            new GroupMemberResponse(
                                    멤버1.getId(),
                                    멤버1.getName(),
                                    관계10.getRelationName(),
                                    멤버1.getIconName(),
                                    멤버1.getIconColor()
                            ),
                            new GroupMemberResponse(
                                    멤버2.getId(),
                                    멤버2.getName(),
                                    관계11.getRelationName(),
                                    멤버2.getIconName(),
                                    멤버2.getIconColor()
                            )
                    ),
                    () -> assertThat(myGroupResponse.groupName()).isEqualTo(관계그룹11.getRelationGroupName()),
                    () -> assertThat(myGroupResponse.members()).containsExactly(
                            new GroupMemberResponse(
                                    멤버3.getId(),
                                    멤버3.getName(),
                                    관계12.getRelationName(),
                                    멤버3.getIconName(),
                                    멤버3.getIconColor()
                            )
                    )
            );
        }
    }
}
