//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.service.impl;

import com.example.hospital.bean.HospitalEntity;
import com.example.hospital.mapper.HospitalMapper;
import com.example.hospital.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalMapper hospitalMapper;

    public HospitalServiceImpl() {
    }

    public void login(HospitalEntity hospitalEntity) {
        this.hospitalMapper.login(hospitalEntity);
    }
}
