package com.hoquangnam45.pharmacy.controller.admin;

import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.pojo.ApiError;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateListingRequest;
import com.hoquangnam45.pharmacy.service.MedicineService;
import com.hoquangnam45.pharmacy.service.S3UploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/medicine")
@PreAuthorize("hasAuthority('ADMIN')")
@Transactional
public class MedicineAdminController {
    private final MedicineService medicineService;
    private final S3UploadService uploadService;

    public MedicineAdminController(MedicineService medicineService, S3UploadService uploadService) {
        this.medicineService = medicineService;
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> addMedicine(
            @RequestBody MedicineDetailCreateRequest productDetail,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Create medicine detail successfully", medicineService.createMedicineDetail(productDetail).getId()));
    }

    @DeleteMapping("{medicineId}")
    public ResponseEntity<GenericResponse> deleteMedicine(
            @PathVariable UUID medicineId,
            HttpServletRequest servletRequest) {
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Delete successfully"));
    }

    @PostMapping("producer")
    public ResponseEntity<GenericResponse> addProducer(
            @RequestBody ProducerCreateRequest createRequest,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(),"Create producer detail successfully", medicineService.createProducer(createRequest).getId()));
    }

    @PostMapping("listing")
    public ResponseEntity<GenericResponse> addListing(
            @RequestBody MedicineListingCreateRequest createRequest,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Create listing successfully", medicineService.createListing(createRequest).getId()));
    }

    @PutMapping("listing/{listingId}")
    public ResponseEntity<GenericResponse> updateListing(
            @PathVariable("listingId") UUID listingId,
            @RequestBody UpdateListingRequest updateListingRequest,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), medicineService.updateListing(listingId, updateListingRequest).getId().toString()));
    }

    @DeleteMapping("listing/{listingId}")
    public ResponseEntity<GenericResponse> deleteListing(
            @PathVariable UUID listingId,
            HttpServletRequest servletRequest) {
        medicineService.deleteListing(listingId);
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Delete listing successfully"));
    }

    @PostMapping("{medicineId}/packaging")
    public ResponseEntity<GenericResponse> addPackaging(
            @PathParam("medicineId") UUID medicineId,
            @RequestBody List<MedicinePackagingRequest> createRequests,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Create packaging successfully", Optional.ofNullable(medicineService.createPackaging(medicineId, createRequests))
                .map(List::stream)
                .orElseThrow(() -> ApiError.notFound("Could not find medicine"))
                .map(MedicinePackaging::getId)
                .collect(Collectors.toList())));
    }

    @PostMapping("images")
    public ResponseEntity<GenericResponse> createUploadSession() {
        return uploadService.
    }

    @PostMapping("images/{sessionId}")
    public ResponseEntity<GenericResponse> addUploadPlaceholder() {

    }

    @PostMapping("images/{sessionId}/{itemId}/upload")
    public ResponseEntity<GenericResponse> uploadFile(@RequestParam("file") MultipartFile file) {

    }
}
