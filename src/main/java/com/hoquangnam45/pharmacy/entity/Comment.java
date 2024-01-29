package com.hoquangnam45.pharmacy.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue
    private UUID id;

    private String content;

    @Column(name = "super_comment_id")
    private UUID superCommentId;

    @JoinColumn(name = "super_comment_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne
    private Comment superComment;

    @OneToMany(mappedBy = "superComment")
    private Set<Comment> childComments;

    @Column(name = "medicine_id")
    private UUID medicineId;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Medicine medicine;

    @Column(name = "created_by")
    private UUID createdBy;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User createdByUser;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
