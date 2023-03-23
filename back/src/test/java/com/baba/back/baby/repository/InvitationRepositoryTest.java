package com.baba.back.baby.repository;

import static com.baba.back.fixture.DomainFixture.아기1;
import static com.baba.back.fixture.DomainFixture.아기2;
import static com.baba.back.fixture.DomainFixture.초대코드정보;
import static org.assertj.core.api.Assertions.assertThat;

import com.baba.back.baby.domain.Baby;
import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.relation.domain.RelationGroup;
import com.baba.back.relation.repository.RelationGroupRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class InvitationRepositoryTest {

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private RelationGroupRepository relationGroupRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Test
    void 코드로_초대_정보를_조회한다() {
        // given
        final Baby savedBaby = babyRepository.save(아기1);
        final RelationGroup savedRelationGroup = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby)
                .relationGroupName("외가")
                .family(false)
                .build());

        final Baby savedBaby2 = babyRepository.save(아기2);
        final RelationGroup savedRelationGroup2 = relationGroupRepository.save(RelationGroup.builder()
                .baby(savedBaby2)
                .relationGroupName("외가")
                .family(false)
                .build());

        final Invitation savedInvitation = invitationRepository.save(Invitation.builder()
                .invitationCode(초대코드정보)
                .relationGroup(savedRelationGroup)
                .build());

        final Invitation savedInvitation2 = invitationRepository.save(Invitation.builder()
                .invitationCode(초대코드정보)
                .relationGroup(savedRelationGroup2)
                .build());

        // when
        final List<Invitation> invitations = invitationRepository.findAllByCode(초대코드정보.getCode().getValue());

        // then
        assertThat(invitations).containsExactly(savedInvitation, savedInvitation2);
    }
}
