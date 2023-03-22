package com.baba.back.baby.domain.invitation;

import com.baba.back.common.domain.BaseEntity;
import com.baba.back.relation.domain.RelationGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    InvitationCode invitationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    RelationGroup relationGroup;

    @Builder
    public Invitation(InvitationCode invitationCode, RelationGroup relationGroup) {
        this.invitationCode = invitationCode;
        this.relationGroup = relationGroup;
    }
}
