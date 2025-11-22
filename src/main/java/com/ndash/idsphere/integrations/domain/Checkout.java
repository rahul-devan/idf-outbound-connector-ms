package com.ndash.idsphere.integrations.domain;

import com.ndash.idsphere.integrations.domain.enums.ProcessType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "checkout")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID checkoutId;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", nullable = false, length = 20)
    private ProcessType processType;

    /**
     * FK: Application
     */
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    /**
     * FK: User
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * CREATED BY — extracted from token
     */
    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    /**
     * CREATED TIME
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    /**
     * IS PROCESSED (true/false)
     */
    @Column(name = "is_processed", nullable = false)
    private boolean processed = false;

    /**
     * REMARKS
     */
    @Column(name = "remarks", length = 255)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
