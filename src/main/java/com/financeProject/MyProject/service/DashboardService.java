package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.CategorySummaryDTO;
import com.financeProject.MyProject.dto.DashboardSummaryDTO;
import com.financeProject.MyProject.model.FinancialRecord;
import com.financeProject.MyProject.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    // GET SUMMARY (Viewer + Analyst + Admin allowed)
    public DashboardSummaryDTO getSummary() {

        List<FinancialRecord> records = recordRepository.findAll();

        double totalIncome = 0;
        double totalExpense = 0;

        // Calculate totals
        for (FinancialRecord r : records) {

            if (r.getType().equals("INCOME")) {
                totalIncome += r.getAmount();
            } else if (r.getType().equals("EXPENSE")) {
                totalExpense += r.getAmount();
            }
        }

        // Prepare DTO
        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setTotalIncome(totalIncome);
        dto.setTotalExpense(totalExpense);
        dto.setNetBalance(totalIncome - totalExpense);

        return dto;
    }


    // CATEGORY-WISE TOTALS
    public List<CategorySummaryDTO> getCategorySummary() {

        List<FinancialRecord> records = recordRepository.findAll();

        // Map to store category → total
        Map<String, Double> categoryMap = new HashMap<>();

        for (FinancialRecord r : records) {

            String category = r.getCategory();

            // If category is null, handle safely
            if (category == null) {
                category = "OTHER";
            }

            // Add amount to existing category total
            categoryMap.put(
                    category,
                    categoryMap.getOrDefault(category, 0.0) + r.getAmount()
            );
        }

        // Convert map → DTO list
        List<CategorySummaryDTO> result = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            CategorySummaryDTO dto = new CategorySummaryDTO();
            dto.setCategory(entry.getKey());
            dto.setTotalAmount(entry.getValue());

            result.add(dto);
        }

        return result;
    }
}