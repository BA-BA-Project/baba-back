package com.baba.back.baby.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class InvitationCodeTest {

    public static final int EXPIRATION_DAYS = 10;

    @Test
    void 초대코드가_만료되지_않았으면_false를_반환한다() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> "AAAAAA"))
                .relationName("외가")
                .now(now)
                .build();

        // when & then
        assertThat(invitationCode.isExpired(now.plusDays(EXPIRATION_DAYS))).isFalse();
    }

    @Test
    void 초대코드가_만료되었으면_true를_반환한다() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final InvitationCode invitationCode = InvitationCode.builder()
                .code(Code.from((length, chars) -> "AAAAAA"))
                .relationName("외가")
                .now(now)
                .build();

        // when & then
        assertThat(invitationCode.isExpired(now.plusDays(EXPIRATION_DAYS).plusSeconds(1))).isTrue();
    }
}