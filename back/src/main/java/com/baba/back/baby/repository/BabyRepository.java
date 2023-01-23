package com.baba.back.baby.repository;

import com.baba.back.baby.domain.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BabyRepository extends JpaRepository<Baby, String> {
}
