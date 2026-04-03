package com.financeProject.MyProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Used for creating/updating financial records
public class FinancialRecordRequestDTO {

    private Double amount;        // Transaction amount
    private String type;          // INCOME / EXPENSE
    private String category;      // e.g., Food, Rent
    private String notes;         // Optional
    private String recordDate;    // Format: YYYY-MM-DD
}
