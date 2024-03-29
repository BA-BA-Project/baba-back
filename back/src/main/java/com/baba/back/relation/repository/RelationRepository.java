package com.baba.back.relation.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.relation.domain.Relation;
import com.baba.back.relation.domain.RelationGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    @Query("select r from Relation r where r.member = :member and r.relationGroup.baby = :baby")
    Optional<Relation> findByMemberAndBaby(@Param("member") Member member,
                                           @Param("baby") Baby baby);

    List<Relation> findAllByMember(Member member);

    List<Relation> findAllByMemberAndRelationGroupFamily(Member member, boolean isFamily);

    Optional<Relation> findFirstByMemberAndRelationGroupFamily(Member member, boolean isFamily);

    Optional<Relation> findFirstByRelationGroup(RelationGroup relationGroup);

    List<Relation> findAllByRelationGroup(RelationGroup relationGroup);

    List<Relation> findAllByRelationGroupIn(List<RelationGroup> relationGroups);
}
