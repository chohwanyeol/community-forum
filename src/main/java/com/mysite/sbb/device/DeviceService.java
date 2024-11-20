package com.mysite.sbb.device;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserService userService;


    public CompletableFuture<Void> CheckDevice(SiteUser siteUser, String deviceInfo, String locationInfo) {
        return CompletableFuture.runAsync(() -> {
            if (deviceInfo.equals("Unknown")) {
                userService.mailTempDeviceInfo(siteUser.getUsername(), siteUser.getEmail(), locationInfo+"<br/>알수 없는 기기");
            } else {
                handleDeviceRegistration(siteUser, deviceInfo,locationInfo);
            }
        });
    }

    // 기기 등록 및 메일 발송
    @Transactional
    public void handleDeviceRegistration(SiteUser siteUser, String deviceInfo, String locationInfo) {
        List<Device> devices = deviceRepository.findByUserAndDeviceInfo(siteUser, deviceInfo,locationInfo).orElse(Collections.emptyList());

        if (devices.equals(Collections.emptyList())) {
            Device device = new Device();
            device.setUser(siteUser);
            device.setCreateDate(LocalDateTime.now());
            device.setDeviceInfo(deviceInfo);
            device.setLocation(locationInfo);
            deviceRepository.save(device);
            userService.mailTempDeviceInfo(siteUser.getUsername(), siteUser.getEmail(), locationInfo+"<br/>"+deviceInfo);
        }
    }

    @Transactional
    public void delete(SiteUser siteUser) {
        deviceRepository.deleteAllByUser(siteUser);
    }
}
