package org.example.wecambackend.repos;

import org.example.model.invitation.InvitationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationHistoryRepository extends JpaRepository<InvitationHistory,Long> {
}
