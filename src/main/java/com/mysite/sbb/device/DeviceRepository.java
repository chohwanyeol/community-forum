package com.mysite.sbb.device;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;




public interface DeviceRepository extends JpaRepository<Device, Integer> {
    @Query("Select D From Device D where D.user = :siteUser and D.deviceInfo = :deviceInfo and D.location = :locationInfo")
    Optional<List<Device>> findByUserAndDeviceInfo(@Param("siteUser") SiteUser siteUser, @Param("deviceInfo") String deviceInfo, String locationInfo);

    Optional<List<Device>> findByUser(SiteUser siteUser);

    void deleteAllByUser(SiteUser siteUser);
}
