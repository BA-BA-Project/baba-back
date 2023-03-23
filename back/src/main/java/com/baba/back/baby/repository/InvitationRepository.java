package com.baba.back.baby.repository;

import com.baba.back.baby.domain.invitation.Invitation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("select i from Invitation i where i.invitationCode.code.value = :code")
    List<Invitation> findAllByCode(@Param("code") String code);
}
