package com.financeProject.MyProject.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Used when returning financial records to client
public class FinancialRecordResponseDTO {

    private Long id;
    private Double amount;
    private String type;
    private String category;
    private String recordDate;

    // Optional: include user info (simple form)
    private Long userId;
}
