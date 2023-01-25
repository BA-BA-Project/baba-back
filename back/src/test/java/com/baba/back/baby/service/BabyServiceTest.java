package com.baba.back.baby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.baby.repository.BabyRepository;
import com.baba.back.oauth.domain.ColorPicker;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.relation.domain.DefaultRelation;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.exception.RelationNotFoundException;
import com.baba.back.relation.repository.RelationRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class BabyServiceTest {
    public static final String MEMBER_ID = "1234";
    public static final String BABY_ID = "1234";

    // 디폴트 아기 조회 API에 대한 테스트

    @Autowired
    private BabyService babyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Test
    void 기본_설정된_아기가_없다면_예외를_던진다() {
        assertThatThrownBy(() -> babyService.searchDefaultBaby(MEMBER_ID))
                .isInstanceOf(RelationNotFoundException.class);
    }

    @Test
    void 디폴트_아기를_조회한다() {
        // given
        LocalDate birthday = LocalDate.of(2024, 1, 25);
        LocalDate now = LocalDate.of(2023, 1, 25);
        final String color = "FFAEBA";
        ColorPicker<String> colorPicker = (colors) -> color;

        Member member = memberRepository.save(Member.builder()
                .id(MEMBER_ID)
                .name("박재희")
                .introduction("")
                .colorPicker(colorPicker)
                .iconName("icon1")
                .build());

        Baby baby = babyRepository.save(Baby.builder()
                .id(BABY_ID)
                .name("앙쥬")
                .birthday(birthday)
                .now(now)
                .build());

        relationRepository.save(Relation.builder()
                .member(member)
                .baby(baby)
                .relationName("엄마")
                .relationGroup(RelationGroup.FAMILY)
                .defaultRelation(DefaultRelation.DEFAULT)
                .build());

        // when
        SearchDefaultBabyResponse response = babyService.searchDefaultBaby(MEMBER_ID);

        // then
        assertThat(response.getBabyId()).isEqualTo(BABY_ID);
    }
}