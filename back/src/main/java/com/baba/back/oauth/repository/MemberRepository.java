package com.baba.back.oauth.repository;

import com.baba.back.oauth.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
