package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.MedicineMapper;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.entity.Tag;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.PartialPage;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateListingRequest;
import com.hoquangnam45.pharmacy.repo.MedicineListingRepo;
import com.hoquangnam45.pharmacy.repo.MedicinePackagingRepo;
import com.hoquangnam45.pharmacy.repo.MedicineRepo;
import com.hoquangnam45.pharmacy.repo.OrderRepo;
import com.hoquangnam45.pharmacy.repo.ProducerRepo;
import com.hoquangnam45.pharmacy.repo.TagRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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

    public MedicineService(MedicineRepo medicineRepo, ProducerRepo producerRepo, MedicineMapper medicineMapper, TagRepo tagRepo, MedicineListingRepo medicineListingRepo, MedicinePackagingRepo medicinePackagingRepo, OrderRepo orderRepo, S3Service s3Service, UploadSessionService uploadSessionService) {
        this.medicineRepo = medicineRepo;
        this.producerRepo = producerRepo;
        this.medicineMapper = medicineMapper;
        this.tagRepo = tagRepo;
        this.medicineListingRepo = medicineListingRepo;
        this.medicinePackagingRepo = medicinePackagingRepo;
        this.orderRepo = orderRepo;
        this.s3Service = s3Service;
        this.uploadSessionService = uploadSessionService;
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
        return medicine;
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
        List<MedicinePackaging> medicinePackagings = medicine.getAllowPackagingUnits();
        medicinePackagings.stream().map(MedicinePackaging::getId).forEach(medicineListingRepo::deleteByPackaging_Id);
        medicinePackagings.forEach(medicinePackagingRepo::delete);
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
