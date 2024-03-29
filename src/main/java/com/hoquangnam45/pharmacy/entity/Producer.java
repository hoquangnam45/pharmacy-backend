package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "producer")
public class Producer {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String country;

    @OneToMany(mappedBy = "producer")
    private Set<Medicine> medicines;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
