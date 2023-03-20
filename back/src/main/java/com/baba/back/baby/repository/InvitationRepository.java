package com.baba.back.baby.repository;

import com.baba.back.baby.domain.invitation.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
