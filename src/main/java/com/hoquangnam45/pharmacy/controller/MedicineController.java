package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.entity.Tag;
import com.hoquangnam45.pharmacy.pojo.MedicineInfo;
import com.hoquangnam45.pharmacy.pojo.PartialPage;
import com.hoquangnam45.pharmacy.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("medicine")
public class MedicineController {
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

//    @GetMapping
//    public ResponseEntity<PartialPage<MedicineInfo>> getMedicineInfo() {
//
//    }
}
