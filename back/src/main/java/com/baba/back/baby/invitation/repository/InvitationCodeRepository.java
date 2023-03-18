package com.baba.back.baby.invitation.repository;

import com.baba.back.baby.invitation.domain.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {
}
