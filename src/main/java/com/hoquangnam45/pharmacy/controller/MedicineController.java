package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.component.mapper.MedicineMapper;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging_;
import com.hoquangnam45.pharmacy.entity.Medicine_;
import com.hoquangnam45.pharmacy.pojo.MedicineFilter;
import com.hoquangnam45.pharmacy.pojo.MedicineListingInfo;
import com.hoquangnam45.pharmacy.pojo.PartialPage;
import com.hoquangnam45.pharmacy.service.MedicineService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.hoquangnam45.pharmacy.service.MedicineService.toSpec;

@RestController
@RequestMapping("medicine")
public class MedicineController {
    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public MedicineController(MedicineService medicineService, MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @GetMapping
    public ResponseEntity<PartialPage<MedicineListingInfo>> getMedicineListingInfo(
            @RequestParam MedicineFilter filter, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        // Why page size + 1 -> a simple way to know if there are more results after this
        Pageable pageable = Pageable.ofSize(size + 1).withPage(page);

        List<MedicineListingInfo> result = medicineService.searchMedicine(
                toSpec(((root, query, criteriaBuilder) -> {
                        // Eager fetch some necessary information
                        root.fetch(Medicine_.allowPackagingUnits).fetch(MedicinePackaging_.listing);
                        root.fetch(Medicine_.tags);
                        return null;
                })), filter, pageable).stream()
                .map(medicineMapper::createMedicineInfo)
                .collect(Collectors.toList());

        if (result.size() > size) {
            return ResponseEntity.ok(PartialPage.<MedicineListingInfo>builder()
                    .data(result.subList(0, result.size() - 1))
                    .more(true)
                    .build());
        } else {
            return ResponseEntity.ok(PartialPage.<MedicineListingInfo>builder()
                    .data(result)
                    .more(false)
                    .build());
        }
    }

    @GetMapping("search")
    public ResponseEntity<PartialPage<String>> searchMedicine(
            @RequestParam("query") String term, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size + 1).withPage(page);
        List<String> result = medicineService.searchMedicine(
                toSpec((root, query, criteriaBuilder) -> null),
                MedicineFilter.builder()
                        .name(term)
                        .build(),
                        pageable).stream()
                .map(Medicine::getName)
                .collect(Collectors.toList());
        if (result.size() > size) {
            return ResponseEntity.ok(PartialPage.<String>builder()
                    .data(result.subList(0, result.size() - 1))
                    .more(true)
                    .build());
        } else {
            return ResponseEntity.ok(PartialPage.<String>builder()
                    .data(result)
                    .more(false)
                    .build());
        }
    }
}
