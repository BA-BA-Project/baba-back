package com.baba.back.baby.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
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

    @Test
    void 멤버가_존재하지않으면_예외를_던진다() {
        // given
        final String memberId = "memberId";
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.searchDefaultBaby(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 기본_설정된_아기가_없다면_예외를_던진다() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.of(멤버1));
        given(relationRepository.findByMemberAndDefaultRelation(any(), anyBoolean())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.searchDefaultBaby("멤버1"))
                .isInstanceOf(RelationNotFoundException.class);
    }


    @Test
    void 기본_설정된_아기를_조회한다() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.of(멤버1));
        given(relationRepository.findByMemberAndDefaultRelation(any(), anyBoolean())).willReturn(Optional.of(관계1));

        // when
        final SearchDefaultBabyResponse response = babyService.searchDefaultBaby("멤버1");

        // then
        assertThat(response.getBabyId()).isEqualTo(아기1.getId());
    }
}
