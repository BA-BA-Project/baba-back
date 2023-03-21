package com.baba.back.baby.domain;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.common.domain.Name;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Baby extends BaseEntity {

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

    public LocalDate getBirthday() {
        return birthday.getBirthday();
    }

    public String getName() {
        return this.name.getValue();
    }
}
