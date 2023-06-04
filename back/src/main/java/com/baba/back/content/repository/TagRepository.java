package com.baba.back.content.repository;

import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.comment.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByComment(Comment comment);

    void deleteByComment(Comment comment);
}
