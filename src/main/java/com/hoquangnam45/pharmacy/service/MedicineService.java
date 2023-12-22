package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.MedicineMapper;
import com.hoquangnam45.pharmacy.controller.admin.MedicineAdminController;
import com.hoquangnam45.pharmacy.entity.FileMetadata;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.MedicinePreview;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.entity.Tag;
import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import com.hoquangnam45.pharmacy.exception.UploadSessionInvalidException;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.PartialPage;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateListingRequest;
import com.hoquangnam45.pharmacy.repo.FileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.MedicineListingRepo;
import com.hoquangnam45.pharmacy.repo.MedicinePackagingRepo;
import com.hoquangnam45.pharmacy.repo.MedicinePreviewRepo;
import com.hoquangnam45.pharmacy.repo.MedicineRepo;
import com.hoquangnam45.pharmacy.repo.OrderRepo;
import com.hoquangnam45.pharmacy.repo.ProducerRepo;
import com.hoquangnam45.pharmacy.repo.TagRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionFileMetadataRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hoquangnam45.pharmacy.util.Functions.peek;
import static java.util.function.Predicate.not;

@Service
public class MedicineService {
    private final MedicineRepo medicineRepo;
    private final ProducerRepo producerRepo;
    private final MedicineMapper medicineMapper;
    private final TagRepo tagRepo;
    private final MedicineListingRepo medicineListingRepo;
    private final MedicinePackagingRepo medicinePackagingRepo;
    private final OrderRepo orderRepo;
    private final S3Service s3Service;
    private final UploadSessionService uploadSessionService;
    private final UploadSessionFileMetadataRepo uploadSessionFileMetadataRepo;
    private final FileMetadataRepo fileMetadataRepo;
    private final MedicinePreviewRepo medicinePreviewRepo;

    public MedicineService(MedicineRepo medicineRepo, ProducerRepo producerRepo, MedicineMapper medicineMapper, TagRepo tagRepo, MedicineListingRepo medicineListingRepo, MedicinePackagingRepo medicinePackagingRepo, OrderRepo orderRepo, S3Service s3Service, UploadSessionService uploadSessionService, UploadSessionFileMetadataRepo uploadSessionFileMetadataRepo, FileMetadataRepo fileMetadataRepo, MedicinePreviewRepo medicinePreviewRepo) {
        this.medicineRepo = medicineRepo;
        this.producerRepo = producerRepo;
        this.medicineMapper = medicineMapper;
        this.tagRepo = tagRepo;
        this.medicineListingRepo = medicineListingRepo;
        this.medicinePackagingRepo = medicinePackagingRepo;
        this.orderRepo = orderRepo;
        this.s3Service = s3Service;
        this.uploadSessionService = uploadSessionService;
        this.uploadSessionFileMetadataRepo = uploadSessionFileMetadataRepo;
        this.fileMetadataRepo = fileMetadataRepo;
        this.medicinePreviewRepo = medicinePreviewRepo;
    }

    public Medicine createMedicineDetail(MedicineDetailCreateRequest createRequest) {
        Producer producer = producerRepo.getReferenceById(createRequest.getProducerId());
        Medicine medicine = medicineMapper.createMedicine(createRequest);
        List<Tag> tags = tagRepo.findByValueIn(createRequest.getTags());
        Set<String> tagNames = tags.stream().map(Tag::getValue).collect(Collectors.toSet());
        List<String> notExistTags = createRequest.getTags().stream().filter(not(tagNames::contains)).toList();
        List<Tag> newTags = notExistTags.stream().map(tag -> Tag.builder().value(tag).build()).map(tagRepo::save).toList();
        tags.addAll(newTags);
        medicine.setProducer(producer);
        medicine.setTags(tags);
        medicine = medicineRepo.save(medicine);
        medicine.setAllowPackagingUnits(createPackaging(medicine, createRequest.getAllowPackagingUnits()));

        // Copy all product preview files to a different location and stored the metadata to db
        UploadSession uploadSession = uploadSessionService.getUploadSession(createRequest.getUploadSessionId());
        if (uploadSession == null) {
            throw new UploadSessionInvalidException("Upload session not exists");
        }
        if (createRequest.getMainPreviewId() == null) {
            throw new UploadSessionInvalidException("Main preview id is missing");
        }
        UploadSessionFileMetadata mainPreviewMetadata = uploadSessionFileMetadataRepo.findByUploadSession_IdAndId(uploadSession.getId(), createRequest.getMainPreviewId());
        if (mainPreviewMetadata == null || mainPreviewMetadata.getFileMetadata() != null) {
            throw new UploadSessionInvalidException("Main preview file not found in session");
        }
        String tempUploadSessionFolder = uploadSessionService.getTempSessionUploadFolder(uploadSession.getType(), uploadSession.getId().toString());
        String medicinePreviewFolder = uploadSessionService.getFinalUploadFolder(MedicineAdminController.MEDICINE_PREVIEW_SESSION_TYPE, medicine.getId().toString());
        s3Service.copyContentOfFolderToFolder(tempUploadSessionFolder, medicinePreviewFolder);
        Medicine finalMedicine = medicine;
        Map<UUID, UUID> uploadSessionFileToFileMetadata = uploadSessionFileMetadataRepo.findAllByUploadSession_Id(uploadSession.getId()).stream()
                .collect(Collectors.toMap(
                        UploadSessionFileMetadata::getId,
                        UploadSessionFileMetadata::getFileMetadataId
                ));
        Map<UUID, MedicinePreview> tempFileMetadataToPreviews = fileMetadataRepo.findAllByUploadSessionFileMetadata_UploadSession_Id(uploadSession.getId()).stream()
                .collect(Collectors.toMap(
                        FileMetadata::getId,
                        fileMetadata -> {
                            String relativePath = Path.of(fileMetadata.getPath()).relativize(Path.of(tempUploadSessionFolder)).toString();
                            String newPath = Path.of(medicinePreviewFolder, relativePath).toString();
                            FileMetadata finalMetadata = new FileMetadata();
                            finalMetadata.setName(fileMetadata.getName());
                            finalMetadata.setExtension(fileMetadata.getExtension());
                            finalMetadata.setContentType(fileMetadata.getContentType());
                            finalMetadata.setCreatedAt(fileMetadata.getCreatedAt());
                            finalMetadata.setPath(newPath);
                            finalMetadata = fileMetadataRepo.save(finalMetadata);
                            MedicinePreview preview = new MedicinePreview();
                            preview.setMedicine(finalMedicine);
                            preview.setFileMetadata(finalMetadata);
                            return preview;
                        }
                ));
        medicine.setMainPreview(tempFileMetadataToPreviews.get(uploadSessionFileToFileMetadata.get(createRequest.getMainPreviewId())));
        tempFileMetadataToPreviews.values().forEach(medicinePreviewRepo::save);
        finalMedicine.setPreviews(new HashSet<>(tempFileMetadataToPreviews.values()));

        // Clean-up upload session
        uploadSessionService.deleteSession(uploadSession);
        
        return finalMedicine;
    }

    public List<MedicinePackaging> createPackaging(Medicine medicine, List<MedicinePackagingRequest> requests) {
        return Optional.ofNullable(requests)
                .stream()
                .flatMap(List::stream)
                .map(request -> {
                    MedicinePackaging ret = medicineMapper.createMedicinePackaging(request);
                    ret.setMedicine(medicine);
                    return Pair.of(request.getListingCreateRequest(), ret);
                })
                .map(pair -> {
                    MedicinePackaging medicinePackaging = medicinePackagingRepo.save(pair.getRight());
                    Optional.ofNullable(pair.getKey())
                            .map(medicineMapper::createMedicineListing)
                            .map(peek(listing -> listing.setPackaging(medicinePackaging)))
                            .map(medicineListingRepo::save)
                            .ifPresent(listing -> {
                                Set<MedicineListing> listings = Optional.ofNullable(medicinePackaging.getListings())
                                        .orElseGet(HashSet::new);
                                listings.add(listing);
                                medicinePackaging.setListings(listings);
                            });
                    return medicinePackaging;
                })
                .collect(Collectors.toList());
    }

    public List<MedicinePackaging> createPackaging(UUID medicineId, List<MedicinePackagingRequest> requests) {
        Medicine medicine = medicineRepo.findById(medicineId).orElse(null);
        if (medicine == null) {
            return null;
        }
        return createPackaging(medicine, requests);
    }

    public Producer createProducer(ProducerCreateRequest createRequest) {
        Producer producer = medicineMapper.createProducer(createRequest);
        return producerRepo.save(producer);
    }

    public MedicineListing createListing(MedicineListingCreateRequest createRequest) {
        MedicineListing medicineListing = medicineMapper.createMedicineListing(createRequest);
        return medicineListingRepo.save(medicineListing);
    }

    public PartialPage<Tag> findTag(String tag, int limit) {
        List<Tag> tags = tagRepo.findAllByValueLike("%" + tag + "%", Pageable.ofSize(limit + 1).first());
        boolean more = tags.size() > limit;
        tags.remove(tags.size() - 1);
        return new PartialPage<>(more, tags);
    }

    public void deleteMedicine(UUID id) {
        Medicine medicine = medicineRepo.findById(id).orElse(null);
        if (medicine == null) {
            return;
        }
        // delete listing
        medicineListingRepo.deleteAllByPackaging_Medicine_Id(id);

        // delete packaging
        medicinePackagingRepo.deleteAllByMedicine_Id(medicine.getId());

        // delete uploaded file
        s3Service.deleteFolder(uploadSessionService.getFinalUploadFolder(MedicineAdminController.MEDICINE_PREVIEW_SESSION_TYPE, id.toString()));
        List<FileMetadata> toBeDeletedFileMetadatas = fileMetadataRepo.findAllByMedicinePreview_Medicine_Id(medicine.getId());
        medicinePreviewRepo.deleteAllByMedicine_Id(medicine.getId());
        toBeDeletedFileMetadatas.forEach(fileMetadataRepo::delete);

        // delete medicine
        medicineRepo.deleteById(id);
    }

    public void deleteListing(UUID listingId) {
        medicineListingRepo.deleteById(listingId);
    }

    public MedicineListing updateListing(UUID id, UpdateListingRequest updateListingRequest) {
        MedicineListing listing = medicineListingRepo.findById(id).orElse(null);
        if (listing == null) {
            return null;
        }
        if (updateListingRequest.getDisable() != null) {
            listing.setDisable(updateListingRequest.getDisable());
        }
        if (updateListingRequest.getPrice() != null) {
            listing.setPrice(updateListingRequest.getPrice());
        }
        return listing;
    }
}
