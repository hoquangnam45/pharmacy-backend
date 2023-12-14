package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import com.hoquangnam45.pharmacy.constant.UsageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "medicine")
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "preview_id", referencedColumnName = "id")
    private MedicinePreview mainPreview;

    @ManyToMany
    @JoinTable(
            name = "medicine_tag_x",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @Enumerated(EnumType.STRING)
    private PackagingUnit basicUnit;
    @OneToMany(mappedBy = "medicine")
    private List<MedicinePackaging> allowPackagingUnits;
    private String sideEffect;

    @Enumerated(EnumType.STRING)
    private UsageType usageType;
    @ManyToOne
    @JoinColumn(name = "producer_id", referencedColumnName = "id")
    private Producer producer;

    @OneToMany(mappedBy = "medicine")
    private Set<MedicinePreview> previews;
}
