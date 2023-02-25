package com.baba.back.oauth.service;

import static com.baba.back.fixture.DomainFixture.이미_회원가입한_유저1;
import static com.baba.back.fixture.DomainFixture.회원가입_안한_유저1;
import static com.baba.back.fixture.RequestFixture.멤버_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.baby.domain.IdConstructor;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.Picker;
import com.baba.back.oauth.domain.member.IconColor;
import com.baba.back.oauth.dto.MemberJoinRequest;
import com.baba.back.oauth.dto.MemberJoinResponse;
import com.baba.back.oauth.exception.JoinedMemberBadRequestException;
import com.baba.back.oauth.exception.JoinedMemberNotFoundException;
import com.baba.back.oauth.repository.JoinedMemberRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.repository.RelationRepository;
import java.util.Optional;
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
    private JoinedMemberRepository joinedMemberRepository;

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private RelationRepository relationRepository;

    @Spy
    private Picker<IconColor> picker;

    @Mock
    private IdConstructor idConstructor;

    @Test
    void 로그인을_하지_않은_멤버는_회원가입을_할_수_없다() {
        // given
        final String invalidMemberId = "memberId";
        given(joinedMemberRepository.findById(invalidMemberId)).willReturn(Optional.empty());

        // when & then
        final MemberJoinRequest request = new MemberJoinRequest();

        Assertions.assertThatThrownBy(() -> memberService.join(request, invalidMemberId))
                .isInstanceOf(JoinedMemberNotFoundException.class);
    }

    @Test
    void 이미_회원가입한_멤버는_회원가입할_수_없다() {
        // given
        given(joinedMemberRepository.findById(anyString())).willReturn(Optional.of(이미_회원가입한_유저1));

        // when & then
        final MemberJoinRequest request = new MemberJoinRequest();

        Assertions.assertThatThrownBy(() -> memberService.join(request, "memberId"))
                .isInstanceOf(JoinedMemberBadRequestException.class);
    }

    @Test
    void 회원가입을_진행한다() {
        // given
        given(joinedMemberRepository.findById(anyString())).willReturn(Optional.of(회원가입_안한_유저1));
        given(picker.pick(anyList())).willReturn(IconColor.COLOR_1);

        final MemberJoinResponse response = memberService.join(멤버_가입_요청, "memberId");

        //then
        then(memberRepository).should(times(1)).save(any());
        then(idConstructor).should(times(1)).createId();
        then(babyRepository).should(times(2)).save(any());
        then(relationRepository).should(times(2)).save(any());

        assertThat(response.result()).isNotBlank();
    }
}
