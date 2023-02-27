package com.baba.back.oauth.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.IconColor;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.repository.RelationRepository;
import java.time.Clock;
import org.assertj.core.api.Assertions;
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
    private RelationRepository relationRepository;

    @Spy
    private Picker<IconColor> picker;

    @Mock
    private Clock clock;

    @Mock
    private IdConstructor idConstructor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Test
    void 이미_회원가입한_멤버는_회원가입할_수_없다() {
        // given
        final String memberId = "memberId";

        given(memberRepository.existsById(memberId)).willReturn(true);

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.signUp(new MemberSignUpRequest(), memberId))
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
        given(picker.pick(anyList())).willReturn(IconColor.COLOR_1);
        given(memberRepository.save(any(Member.class))).willReturn(멤버1);
        given(idConstructor.createId()).willReturn(아기1.getId(), 아기2.getId());
        given(clock.instant()).willReturn(now.instant());
        given(clock.getZone()).willReturn(now.getZone());
        given(babyRepository.save(any(Baby.class))).willReturn(아기1, 아기2);
        given(relationRepository.save(any(Relation.class))).willReturn(관계1, 관계2);
        given(accessTokenProvider.createToken(memberId)).willReturn(accessToken);
        given(refreshTokenProvider.createToken(memberId)).willReturn(refreshToken);

        // when
        final MemberSignUpResponse response = memberService.signUp(멤버_가입_요청, memberId);

        //then
        then(memberRepository).should(times(1)).save(any());
        then(idConstructor).should(times(2)).createId();
        then(babyRepository).should(times(2)).save(any());
        then(relationRepository).should(times(2)).save(any());

        assertAll(
                () -> assertThat(response.accessToken()).isEqualTo(accessToken),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );
    }
}
