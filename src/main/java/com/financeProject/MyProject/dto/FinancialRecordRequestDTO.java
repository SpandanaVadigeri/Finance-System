package com.financeProject.MyProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Used for creating/updating financial records
public class FinancialRecordRequestDTO {

    private Double amount;        // Transaction amount
    private String type;          // INCOME / EXPENSE
    private String category;      // e.g., Food, Rent
    private String notes;         // Optional
    private LocalDate recordDate;    // Format: YYYY-MM-DD
}
