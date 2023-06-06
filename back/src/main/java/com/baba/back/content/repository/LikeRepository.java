package com.baba.back.content.repository;

import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByContentAndMember(Content content, Member member);

    @Query("select l from Like l where l.content = :content and l.deleted = false")
    List<Like> findAllByContent(@Param("content") Content content);
}
