package com.baba.back.relation.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.relation.domain.RelationGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationGroupRepository extends JpaRepository<RelationGroup, Long> {

    Optional<RelationGroup> findByBabyAndRelationGroupNameValue(Baby baby, String relationGroupName);
}
