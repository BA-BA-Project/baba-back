package com.baba.back.baby.invitation.repository;

import com.baba.back.baby.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
