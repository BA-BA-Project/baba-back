package com.baba.back.oauth.domain.baby;

import com.baba.back.oauth.domain.member.Name;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Baby {

    @Id
    private String id;

    @Embedded
    private Name name;

    @Embedded
    private Birthday birthday;

    @Builder
    public Baby(String id, String name, LocalDate birthday, LocalDate now) {
        this.id = id;
        this.name = new Name(name);
        this.birthday = Birthday.of(birthday, now);
    }
}
