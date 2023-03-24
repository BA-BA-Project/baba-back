package com.baba.back.content.repository;

import com.baba.back.content.domain.Like;
import com.baba.back.content.domain.content.Content;
import com.baba.back.oauth.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByContentAndMember(Content content, Member member);

    boolean existsByContentAndMember(Content content, Member member);

    List<Like> findAllByContent(Content content);
}
