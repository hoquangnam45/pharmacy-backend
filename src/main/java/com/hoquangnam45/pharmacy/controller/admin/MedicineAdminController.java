package com.hoquangnam45.pharmacy.controller.admin;

import com.hoquangnam45.pharmacy.component.mapper.MedicineMapper;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.FileMetadata;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateListingRequest;
import com.hoquangnam45.pharmacy.pojo.UploadSessionCreateResponse;
import com.hoquangnam45.pharmacy.service.IS3Service;
import com.hoquangnam45.pharmacy.service.MedicineService;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/medicine")
@PreAuthorize("hasAuthority('ADMIN')")
@Transactional
public class MedicineAdminController {
    public static final String MEDICINE_PREVIEW_SESSION_TYPE = "medicine_preview";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".webm", ".gif", ".png");
    private static final int MAXIMUM_FILE_SIZE_IN_MB = 15;
    private static final long MAXIMUM_FILE_SIZE_IN_BYTE = MAXIMUM_FILE_SIZE_IN_MB * 1024 * 1024;
    private final Tika tika;
    private final MedicineService medicineService;
    private final IS3Service s3Service;
    private final UploadSessionService uploadSessionService;
    private final MedicineMapper medicineMapper;

    public MedicineAdminController(Tika tika, MedicineService medicineService, IS3Service s3Service, UploadSessionService uploadSessionService, MedicineMapper medicineMapper) {
        this.tika = tika;
        this.medicineService = medicineService;
        this.s3Service = s3Service;
        this.uploadSessionService = uploadSessionService;
        this.medicineMapper = medicineMapper;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> addMedicine(
            @RequestBody MedicineDetailCreateRequest productDetail,
            HttpServletRequest servletRequest) throws IOException {
        return ResponseEntity.ok().body(new GenericResponse(HttpStatus.OK, servletRequest.getServletPath(), "Create medicine detail successfully", medicineService.createMedicineDetail(productDetail).getId()));
    }

    @DeleteMapping("{medicineId}")
    public ResponseEntity<GenericResponse> deleteMedicine(
            @PathVariable UUID medicineId,
            HttpServletRequest servletRequest) throws IOException {
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

    @PostMapping("upload")
    public ResponseEntity<UploadSessionCreateResponse> createUploadSession() {
        UploadSession uploadSession = uploadSessionService.createUploadSession(MEDICINE_PREVIEW_SESSION_TYPE);
        return ResponseEntity.ok().body(new UploadSessionCreateResponse(
                uploadSession.getId(),
                uploadSession.getType(),
                uploadSession.getUploadSessionFileMetadatas().stream()
                        .map(UploadSessionFileMetadata::getId)
                        .collect(Collectors.toSet())));
    }

    @PostMapping("upload/{sessionId}/{itemId}")
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable("sessionId") UUID sessionId,
            @PathVariable("itemId") UUID fileId) throws IOException, MimeTypeException {
        if (uploadSessionService.hasSessionExpired(sessionId)) {
            throw ApiError.notFound("Session either expired or not started yet");
        } else if (!uploadSessionService.allowItemId(sessionId, fileId)) {
            throw ApiError.badRequest("Not allowed to upload using file id " + fileId);
        } else if (MAXIMUM_FILE_SIZE_IN_BYTE < file.getSize()) {
            throw ApiError.badRequest("Exceeded maximum file size");
        } else {
            String detectedMimeType = tika.detect(file.getInputStream());
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType type = allTypes.forName(detectedMimeType);
            String fileExtension = type.getExtension().split("\\.")[1];
            if (!ALLOWED_EXTENSIONS.contains("." + fileExtension)) {
                throw ApiError.badRequest(MessageFormat.format("Not allowed uploaded file with detected mime type {0} and extension {1}", detectedMimeType, fileExtension));
            } else {
                String fileName = fileId + "." + fileExtension;
                String tempFileUploadFolder = uploadSessionService.getTempItemSessionUploadFolder(MEDICINE_PREVIEW_SESSION_TYPE, sessionId, fileId);
                String tempFileUploadKey = MessageFormat.format("{0}/{1}", tempFileUploadFolder, fileName);
                FileMetadata resp = medicineMapper.createResponseFileMetadata(uploadSessionService.storeTempFileMetadata(
                        MEDICINE_PREVIEW_SESSION_TYPE,
                        sessionId,
                        fileId,
                        fileName,
                        fileExtension,
                        detectedMimeType,
                        tempFileUploadKey), sessionId, fileId, s3Service.getDownloadPath(tempFileUploadKey));
                s3Service.uploadFile(file, tempFileUploadKey);
                uploadSessionService.renewSession(MEDICINE_PREVIEW_SESSION_TYPE, sessionId);
                return ResponseEntity.ok(resp);
            }
        }
    }

    // Upload session has a fixed time-out, after that time-out expired the temporary session folder is free to
    // be deleted by a scheduled job, to prevent the session folder from being deleted while the session is still active
    // fe client will need to periodically call to this api (kind of like heartbeat) to reset the timeout
    // clock and let the backend know that the session is still active
    @PostMapping("upload/{sessionId}/renew")
    public ResponseEntity<GenericResponse> renewSession(
            @PathVariable("sessionId") UUID sessionId,
            HttpServletRequest request) {
        if (uploadSessionService.hasSessionExpired(sessionId)) {
            throw ApiError.notFound("Session either expired or not started yet");
        } else {
            String path = request.getServletPath();
            uploadSessionService.renewSession(MEDICINE_PREVIEW_SESSION_TYPE, sessionId);
            return ResponseEntity.ok(new GenericResponse(200, path, "Renew session successfully"));
        }
    }

    @DeleteMapping("upload/{sessionId}/{itemId}")
    public ResponseEntity<GenericResponse> deleteUploadedFile(
            @PathVariable("sessionId") UUID sessionId,
            @PathVariable("itemId") UUID fileId,
            HttpServletRequest request) throws IOException {
        String path = request.getServletPath();
        s3Service.deleteFolder(uploadSessionService.getTempItemSessionUploadFolder(MEDICINE_PREVIEW_SESSION_TYPE, sessionId, fileId));
        return ResponseEntity.ok().body(new GenericResponse(200, path, "Delete item successfully"));
    }
}
