package com.financeProject.MyProject.repository;

import com.financeProject.MyProject.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Get all records of a particular user
    List<FinancialRecord> findByUserId(Long userId);

    // Filter by type (INCOME / EXPENSE)
    List<FinancialRecord> findByType(String type);

    // Filter by category
    List<FinancialRecord> findByCategory(String category);
}
