package org.example.wecambackend.repos.invitation;

import org.example.model.invitation.InvitationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationHistoryRepository extends JpaRepository<InvitationHistory,Long> {
    List<InvitationHistory> findByInvitationPkId(Long invitationId);
}
