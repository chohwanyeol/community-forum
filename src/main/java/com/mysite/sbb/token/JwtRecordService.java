package com.mysite.sbb.token;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.device.DeviceRepository;
import com.mysite.sbb.device.DeviceService;
import com.mysite.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtRecordService {
    private final JwtRecordRepository jwtRecordRepository;
    private final DeviceService deviceService;



    @Transactional
    public JwtRecord getUser(SiteUser siteUser) {
        Optional<JwtRecord> jwtRecord = this.jwtRecordRepository.findByUser(siteUser);
        if (jwtRecord.isPresent()){
            return jwtRecord.get();
        }
        else{
            throw new DataNotFoundException("token not found");
        }
    }

    @Transactional
    public void change(JwtRecord jwtRecord) {
        jwtRecord.setCreateDate(LocalDateTime.now());
        jwtRecordRepository.save(jwtRecord);
    }

    @Transactional
    public void create(SiteUser siteUser) {
        JwtRecord jwtRecord = new JwtRecord();
        jwtRecord.setCreateDate(LocalDateTime.now());
        jwtRecord.setUser(siteUser);
        jwtRecordRepository.save(jwtRecord);
    }
}
