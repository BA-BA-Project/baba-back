package com.baba.back.content.repository;

import com.baba.back.content.domain.comment.Comment;
import com.baba.back.content.domain.content.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByContent(Content content);
}
