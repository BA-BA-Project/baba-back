package com.baba.back.baby.domain.invitation;

import static com.baba.back.fixture.DomainFixture.nowDateTime;
import static com.baba.back.fixture.DomainFixture.초대10;
import static com.baba.back.fixture.DomainFixture.초대20;
import static com.baba.back.fixture.DomainFixture.초대21;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationsBadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvitationsTest {

    @Test
    void 초대_리스트의_길이가_0이면_예외를_던진다() {
        // given
        List<Invitation> invitations = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> new Invitations(invitations))
                .isInstanceOf(InvitationsBadRequestException.class);
    }

    @Test
    void 초대_리스트의_초대코드가_동일하지_않으면_예외를_던진다() {
        // given
        List<Invitation> invitations = List.of(초대10, 초대21);

        // when & then
        assertThatThrownBy(() -> new Invitations(invitations))
                .isInstanceOf(InvitationsBadRequestException.class);
    }

    @Test
    void invitations_객체를_생성한다() {
        assertThatCode(() -> new Invitations(List.of(초대10, 초대20)))
                .doesNotThrowAnyException();
    }

    @Test
    void 만료되지_않은_초대코드_요청_시_초대코드가_만료됐으면_예외를_던진다() {
        // given
        final Invitations invitations = new Invitations(List.of(초대10, 초대20));

        // when & then
        assertThatThrownBy(() -> invitations.getUnExpiredInvitationCode(nowDateTime.plusDays(10).plusSeconds(1)))
                .isInstanceOf(InvitationCodeBadRequestException.class);
    }

    @Test
    void 만료되지_않은_초대코드_요청_시_초대코드를_응답한다() {
        // given
        final Invitations invitations = new Invitations(List.of(초대10, 초대20));

        // when
        final InvitationCode invitationCode = invitations.getUnExpiredInvitationCode(nowDateTime.plusDays(10));

        // then
        assertThat(invitationCode).isEqualTo(초대10.getInvitationCode());
    }
}