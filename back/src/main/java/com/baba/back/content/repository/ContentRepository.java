package com.baba.back.content.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.domain.content.ContentDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
    boolean existsByContentDateAndBaby(ContentDate contentDate, Baby baby);
}
