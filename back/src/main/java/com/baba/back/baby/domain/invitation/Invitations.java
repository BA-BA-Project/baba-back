package com.baba.back.baby.domain.invitation;

import com.baba.back.baby.exception.InvitationCodeBadRequestException;
import com.baba.back.baby.exception.InvitationsBadRequestException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Invitations(List<Invitation> values) {
    private static final int ALL_ELEMENTS_EQUAL = 1;

    public Invitations(List<Invitation> values) {
        validateEmpty(values);
        validateInvitations(values);
        this.values = new ArrayList<>(values);
    }

    private void validateEmpty(List<Invitation> values) {
        if (values.isEmpty()) {
            throw new InvitationsBadRequestException("invitations의 길이는 0일 수 없습니다.");
        }
    }

    private void validateInvitations(List<Invitation> values) {
        final Set<InvitationCode> invitationCodes = values.stream()
                .map(Invitation::getInvitationCode)
                .collect(Collectors.toSet());

        if (invitationCodes.size() != ALL_ELEMENTS_EQUAL) {
            throw new InvitationsBadRequestException("invitations는 초대 코드가 동일해야 합니다.");
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
