package com.baba.back.oauth.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void update_메서드_호출_시_멤버의_정보를_변경한다() {
        // given
        final Member member = Member.builder()
                .name("name")
                .introduction("")
                .iconColor(Color.from("#FFAEBA"))
                .iconName("PROFILE_W_1")
                .build();

        final String newName = "name2";
        final String newIntroduction = "newIntroduction";
        final Color newColor = Color.from("#5BD2FF");
        final String newIconName = "PROFILE_W_2";

        // when
        member.update(newName, newIntroduction, newColor, newIconName);

        // then
        Assertions.assertThat(member).extracting("name", "introduction", "iconColor", "iconName")
                .containsExactly(newName, newIntroduction, newColor.getValue(), newIconName);
    }
}