package com.baba.back.invitation.repository;

import com.baba.back.invitation.domain.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {
}
