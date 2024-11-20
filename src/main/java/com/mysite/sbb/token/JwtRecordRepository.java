package com.mysite.sbb.token;


import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtRecordRepository extends JpaRepository<JwtRecord, Integer> {
    Optional<JwtRecord> findByUser(SiteUser siteUser);
}
