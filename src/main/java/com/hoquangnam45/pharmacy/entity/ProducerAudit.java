package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "producer_audit")
public class ProducerAudit {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String country;

    @OneToMany(mappedBy = "producer")
    private Set<MedicineAudit> medicines;

    @Embedded
    private AuditInfo auditInfo;
}
