package com.baba.back.relation.repository;

import com.baba.back.oauth.domain.member.Member;
import com.baba.back.relation.domain.Relation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    List<Relation> findAllByMember(Member member);
}
