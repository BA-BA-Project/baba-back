package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.baby.dto.BabyRequest;
import com.baba.back.oauth.domain.JoinedMember;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.dto.MemberJoinRequest;
import com.baba.back.oauth.dto.MemberJoinResponse;
import com.baba.back.oauth.exception.JoinedMemberBadRequestException;
import com.baba.back.oauth.exception.JoinedMemberNotFoundException;
import com.baba.back.oauth.repository.JoinedMemberRepository;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.repository.RelationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
class MemberServiceTest {

    private final String memberId = "memberId";
    @Autowired
    private MemberService memberService;
    @Autowired
    private JoinedMemberRepository joinedMemberRepository;
    @Autowired
    private RelationRepository relationRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        joinedMemberRepository.save(new JoinedMember(memberId, false));
    }

    @Test
    void 로그인을_하지_않은_멤버는_회원가입을_할_수_없다() {
        // given
        final MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        String memberId = "invalidMemberId";

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.join(memberJoinRequest, memberId))
                .isInstanceOf(JoinedMemberNotFoundException.class);
    }

    @Test
    void 이미_회원가입한_멤버는_회원가입할_수_없다() {
        // given
        final MemberJoinRequest request = new MemberJoinRequest();
        final String invalidMemberId = "invalidMemberId";
        joinedMemberRepository.save(new JoinedMember(invalidMemberId, true));

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.join(request, invalidMemberId))
                .isInstanceOf(JoinedMemberBadRequestException.class);
    }

    @Test
    void 회원가입을_진행한다() {
        // given
        final MemberJoinRequest request = new MemberJoinRequest("박재희", "icon1", "엄마",
                List.of(new BabyRequest("아기1", LocalDate.of(2022, 1, 1)),
                        new BabyRequest("아기2", LocalDate.of(2023, 1, 1)))
        );

        // when
        final MemberJoinResponse response = memberService.join(request, memberId);

        // then
        final Member member = memberRepository.findById(memberId).orElseThrow();
        final List<Relation> relations = relationRepository.findAllByMember(member);

        assertAll(
                () -> assertThat(response.getSignedUp()).isTrue(),
                () -> assertThat(response.getMessage()).isNotBlank(),
                () -> assertThat(relations).hasSize(2)
        );
    }
}
