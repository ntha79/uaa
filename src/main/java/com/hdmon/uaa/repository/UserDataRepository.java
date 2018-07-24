package com.hdmon.uaa.repository;

import com.hdmon.uaa.domain.UserData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the UserData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

}
