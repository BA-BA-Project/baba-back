package com.baba.back.invitation.domain;

import com.baba.back.oauth.domain.member.Name;
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
public class InvitationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private InviteCode inviteCode;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "relation_name"))
    private Name relationName;

    @Embedded
    private Expiration expiration;

    @Builder
    public InvitationCode(String inviteCode, String relationName, LocalDateTime now) {
        this.inviteCode = new InviteCode(inviteCode);
        this.relationName = new Name(relationName);
        this.expiration = Expiration.from(now);
    }
}
