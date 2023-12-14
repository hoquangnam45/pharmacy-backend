package com.hoquangnam45.pharmacy.component.job;

import com.hoquangnam45.pharmacy.controller.admin.MedicineAdminController;
import com.hoquangnam45.pharmacy.service.S3Service;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupUploadSessionFileJob {
    private final UploadSessionService uploadSessionService;
    private final S3Service s3Service;

    public CleanupUploadSessionFileJob(UploadSessionService uploadSessionService, S3Service s3Service) {
        this.uploadSessionService = uploadSessionService;
        this.s3Service = s3Service;
    }

    // Run every 15 minutes
    @Scheduled(cron = "*/15 * * * *")
    public void cleanupTempMedicinePreviewUploadSessionFile() {
        s3Service.listFolderIn(uploadSessionService.getTempTypeUploadFolder(MedicineAdminController.MEDICINE_PREVIEW_SESSION_TYPE))
                .stream()
                .map(commonPrefix -> commonPrefix.split("/"))
                .map(tokens -> tokens[tokens.length - 1])
                .filter(sessionFolderName -> uploadSessionService.hasSessionExpired(MedicineAdminController.MEDICINE_PREVIEW_SESSION_TYPE, sessionFolderName))
                .forEach(sessionFolderName -> s3Service.deleteFolder(uploadSessionService.getTempSessionUploadFolder(MedicineAdminController.MEDICINE_PREVIEW_SESSION_TYPE, sessionFolderName)));
    }
}
