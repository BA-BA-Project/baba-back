package com.baba.back.content.repository;

import com.baba.back.content.domain.comment.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
