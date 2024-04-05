package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import com.hoquangnam45.pharmacy.constant.UsageType;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "medicine_audit")
public class MedicineAudit {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private String shortDescription;

    @ManyToMany
    @JoinTable(
            name = "medicine_tag_audit_x",
            joinColumns = @JoinColumn(name = "medicine_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @Enumerated(EnumType.STRING)
    private PackagingUnit basicUnit;

    @OneToMany(mappedBy = "medicine")
    private List<MedicinePackagingAudit> allowPackagingUnits;

    private String sideEffect;

    @Enumerated(EnumType.STRING)
    private UsageType usageType;

    @ManyToOne
    @JoinColumn(name = "producer_id", referencedColumnName = "id")
    private ProducerAudit producer;

    @OneToMany(mappedBy = "medicine")
    private Set<MedicinePreviewAudit> previews;

    @Embedded
    private AuditInfo auditInfo;
}
