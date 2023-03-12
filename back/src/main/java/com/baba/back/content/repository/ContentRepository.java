package com.baba.back.content.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.content.Content;
import com.baba.back.content.domain.content.ContentDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {
    boolean existsByContentDateAndBaby(ContentDate contentDate, Baby baby);

    @Query("select c from Content c "
            + "where c.baby = :baby and year(c.contentDate.value) = :year and month(c.contentDate.value) = :month")
    List<Content> findByBabyYearAndMonth(@Param("baby") Baby baby, @Param("year") int year, @Param("month") int month);
}
