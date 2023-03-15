package com.baba.back.content.repository;

import com.baba.back.baby.domain.Baby;
import com.baba.back.content.domain.content.Content;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {
    boolean existsByBabyAndContentDateValue(Baby baby, LocalDate contentDate);

    @Query("select c from Content c "
            + "where c.baby = :baby and year(c.contentDate.value) = :year and month(c.contentDate.value) = :month")
    List<Content> findByBabyYearAndMonth(@Param("baby") Baby baby, @Param("year") int year, @Param("month") int month);
}
