//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.controller;

import com.example.hospital.bean.HospitalEntity;
import com.example.hospital.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/hospital"})
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    public HospitalController() {
    }

    @RequestMapping({"/index"})
    public String login(HospitalEntity hospitalEntity) {
        this.hospitalService.login(hospitalEntity);
        return "index";
    }
}
