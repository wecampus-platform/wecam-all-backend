package org.example.wecamadminbackend.repos;

import org.example.model.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformationRepository extends JpaRepository<UserInformation,Long> {
}
