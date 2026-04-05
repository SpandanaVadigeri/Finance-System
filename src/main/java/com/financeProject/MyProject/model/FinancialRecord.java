package com.financeProject.MyProject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "financial_records")
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String type; // INCOME / EXPENSE

    private String category;

    @Column(nullable = false)
    private LocalDate recordDate;

    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.deleted = false;

        if (this.recordDate == null) {
            this.recordDate = LocalDate.now();
        }
    }

    @Column(nullable = false)
    private Boolean deleted = false; // used for soft delete


}