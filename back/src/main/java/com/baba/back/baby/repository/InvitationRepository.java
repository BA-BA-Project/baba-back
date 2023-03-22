package com.baba.back.baby.repository;

import com.baba.back.baby.domain.invitation.Invitation;
import com.baba.back.baby.domain.invitation.InvitationCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByInvitationCode(InvitationCode invitationCode);
}
