package org.example.model.common;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    protected Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.status = Status.INACTIVE;
    }

    public void updateActive() {
        this.status = Status.ACTIVE;
    }

    public void updateInactive() {
        this.status = Status.INACTIVE;
    }
}
