package com.baba.back.baby.domain.invitation;

import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record Invitations(List<Invitation> values) {
    public Invitations(List<Invitation> values) {
        validateEmpty(values);
        this.values = new ArrayList<>(values);
    }

    private void validateEmpty(List<Invitation> values) {
        if (values.isEmpty()) {
            throw new InvitationNotFoundException("초대 정보가 존재하지 않습니다.");
        }
    }

    public InvitationCode getUnExpiredInvitationCode(LocalDateTime now) {
        return this.values.stream()
                .map(Invitation::getInvitationCode)
                .filter(invitationCode -> !invitationCode.isExpired(now))
                .findAny()
                .orElseThrow(() -> new InvitationCodeBadRequestException("초대 코드가 만료되었습니다."));
    }
}
