package com.baba.back.baby.service;

import static com.baba.back.fixture.Fixture.관계1;
import static com.baba.back.fixture.Fixture.멤버1;
import static com.baba.back.fixture.Fixture.아기1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.searchDefaultBaby(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 기본_설정된_아기가_없다면_예외를_던진다() {
        // given
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(멤버1));
        when(relationRepository.findByMemberAndDefaultRelation(any(), anyBoolean())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> babyService.searchDefaultBaby("멤버1"))
                .isInstanceOf(RelationNotFoundException.class);
    }


    @Test
    void 기본_설정된_아기를_조회한다() {
        // given
        doReturn(Optional.of(멤버1)).when(memberRepository).findById(anyString());
        doReturn(Optional.of(관계1)).when(relationRepository).findByMemberAndDefaultRelation(any(), anyBoolean());

        // when
        final SearchDefaultBabyResponse response = babyService.searchDefaultBaby("멤버1");

        // then
        assertThat(response.getBabyId()).isEqualTo(아기1.getId());

    }
}
