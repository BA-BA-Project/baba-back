package com.baba.back.baby.domain.invitation;

import com.baba.back.common.domain.Name;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class InvitationCode {

    private Code code;

    @AttributeOverride(name = "value", column = @Column(name = "relation_name"))
    private Name relationName;

    private Expiration expiration;

    @Builder
    public InvitationCode(Code code, String relationName, LocalDateTime now) {
        this.code = code;
        this.relationName = new Name(relationName);
        this.expiration = Expiration.from(now);
    }

    public String getCode() {
        return this.code.getValue();
    }

    public String getRelationName() {
        return this.relationName.getValue();
    }

    // 삭제 시 오류 발생
    public LocalDateTime getExpiration() {
        return this.expiration.getValue();
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiration.isExpired(now);
    }

    public void updateCode(String code) {
        this.code = Code.from((length, chars) -> code);
    }
}
