package com.baba.back.baby.service;

import static com.baba.back.fixture.DomainFixture.관계1;
import static com.baba.back.fixture.DomainFixture.관계2;
import static com.baba.back.fixture.DomainFixture.관계3;
import static com.baba.back.fixture.DomainFixture.관계4;
import static com.baba.back.fixture.DomainFixture.관계그룹1;
import static com.baba.back.fixture.DomainFixture.관계그룹2;
import static com.baba.back.fixture.DomainFixture.관계그룹3;
import static com.baba.back.fixture.DomainFixture.관계그룹4;
import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.아기3;
import static com.baba.back.fixture.DomainFixture.아기4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyResponse;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.repository.RelationRepository;
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
        given(relationRepository.findByMember(멤버1)).willReturn(List.of(관계2, 관계4, 관계1, 관계3));

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
}
