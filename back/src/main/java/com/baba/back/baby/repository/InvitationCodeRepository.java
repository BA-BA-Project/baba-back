package com.baba.back.baby.repository;

import com.baba.back.baby.domain.invitation.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {
}
