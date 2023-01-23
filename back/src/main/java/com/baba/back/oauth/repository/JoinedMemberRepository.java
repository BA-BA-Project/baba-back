package com.baba.back.oauth.repository;

import com.baba.back.oauth.domain.JoinedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinedMemberRepository extends JpaRepository<JoinedMember, String> {

}
