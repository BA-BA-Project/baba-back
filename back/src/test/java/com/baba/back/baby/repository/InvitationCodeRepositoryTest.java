package com.baba.back.baby.repository;

import static com.baba.back.fixture.DomainFixture.초대코드정보;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.invitation.InvitationCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class InvitationCodeRepositoryTest {

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    @Test
    void 코드_값으로_초대_코드를_조회한다() {
        // given
        final InvitationCode savedInvitationCode = invitationCodeRepository.save(초대코드정보);

        // when
        final InvitationCode invitationCode = invitationCodeRepository.findByCodeValue(초대코드정보.getCode().getValue())
                .orElseThrow();

        // then
        assertThat(savedInvitationCode).isEqualTo(invitationCode);
    }
}