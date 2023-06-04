package com.baba.back.baby.domain.invitation;

import static com.baba.back.fixture.DomainFixture.관계그룹11;
import static com.baba.back.fixture.DomainFixture.초대코드정보1;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvitationTest {

    @Test
    void 초대코드를_변경한다() {
        // given
        final String afterCode = "BBBBBB";

        final Invitation invitation = Invitation.builder()
                .invitationCode(초대코드정보1)
                .relationGroup(관계그룹11)
                .build();

        // when
        invitation.updateCode(afterCode);

        // then
        assertThat(invitation.getInvitationCode().getCode()).isEqualTo(afterCode);
    }
}