package com.baba.back.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.invitation.domain.InviteCode;
import org.junit.jupiter.api.Test;

class InviteCodeGeneratorTest {

    @Test
    void 초대코드를_생성한다() {
        // given & when
        final String code = InviteCodeGenerator.generate();

        // then
        assertThat(code).hasSize(InviteCode.INVITE_CODE_LENGTH);
    }

}