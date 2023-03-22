package com.baba.back.baby.domain.invitation;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.common.domain.Name;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class InvitationCode extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Code code;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "relation_name"))
    private Name relationName;

    @Embedded
    private Expiration expiration;

    @Builder
    public InvitationCode(Code code, String relationName, LocalDateTime now) {
        this.code = code;
        this.relationName = new Name(relationName);
        this.expiration = Expiration.from(now);
    }

    public String getRelationName() {
        return this.relationName.getValue();
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiration.isExpired(now);
    }
}
