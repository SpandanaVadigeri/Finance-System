package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.CategorySummaryDTO;
import com.financeProject.MyProject.dto.DashboardSummaryDTO;
import com.financeProject.MyProject.dto.FinancialRecordResponseDTO;
import com.financeProject.MyProject.model.FinancialRecord;
import com.financeProject.MyProject.model.User;
import com.financeProject.MyProject.repository.FinancialRecordRepository;
import com.financeProject.MyProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

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

    public Object getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getName();

        // VIEWER → own summary
        if (role.equals("VIEWER")) {

            List<FinancialRecord> records =
                    recordRepository.findByUserId(user.getId());

            return calculateSummary(records);
        }

        // ANALYST / ADMIN → all viewers summary (individual)
        List<User> viewers = userRepository.findByRoleName("VIEWER");

        List<Object> result = new ArrayList<>();

        for (User viewer : viewers) {

            List<FinancialRecord> records =
                    recordRepository.findByUserId(viewer.getId());

            Object summary = calculateSummary(records);

            result.add(Map.of(
                    "userId", viewer.getId(),
                    "name", viewer.getName(),
                    "summary", summary
            ));
        }

        return result;
    }

    public Object getCompanySummary(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getName();

        // VIEWER not allowed
        if (role.equals("VIEWER")) {
            throw new RuntimeException("Access denied");
        }

        //  ANALYST / ADMIN → all records combined
        List<FinancialRecord> allRecords =
                recordRepository.findAll();

        return calculateSummary(allRecords);
    }

    private Map<String, Double> calculateSummary(List<FinancialRecord> records) {

        double income = 0;
        double expense = 0;

        for (FinancialRecord r : records) {
            if (r.getType().equals("INCOME")) {
                income += r.getAmount();
            } else {
                expense += r.getAmount();
            }
        }

        return Map.of(
                "totalIncome", income,
                "totalExpense", expense,
                "netBalance", income - expense
        );
    }

    public Object getTrends(String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        String role = user.getRole().getName();

        List<FinancialRecord> records;

        if (role.equals("VIEWER")) {
            records = recordRepository.findByUserId(user.getId());
        } else {
            records = recordRepository.findAll();
        }

        // Group by month
        Map<String, Double> trends = new HashMap<>();

        for (FinancialRecord r : records) {
            YearMonth month = YearMonth.from(r.getRecordDate());

            trends.put(String.valueOf(month),
                    trends.getOrDefault(month, 0.0) + r.getAmount());
        }

        return trends;
    }

    public Object getCategoryAnalysis(String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        String role = user.getRole().getName();

        List<FinancialRecord> records;

        if (role.equals("VIEWER")) {
            records = recordRepository.findByUserId(user.getId());
        } else {
            records = recordRepository.findAll();
        }

        Map<String, Double> categoryMap = new HashMap<>();

        for (FinancialRecord r : records) {
            String category = r.getCategory();

            categoryMap.put(category,
                    categoryMap.getOrDefault(category, 0.0) + r.getAmount());
        }

        return categoryMap;
    }

    public Object getRecentActivity(String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        String role = user.getRole().getName();

        List<FinancialRecord> records;

        if (role.equals("VIEWER")) {
            records = recordRepository.findByUserId(user.getId());
        } else {
            records = recordRepository.findAll();
        }

        // Sort by date (latest first)
        records.sort((a, b) -> b.getRecordDate().compareTo(a.getRecordDate()));

        // Return top 5 recent
        return records.stream()
                .limit(5)
                .map(this::convertToDTO)
                .toList();
    }

    private FinancialRecordResponseDTO convertToDTO(FinancialRecord record) {

        FinancialRecordResponseDTO dto = new FinancialRecordResponseDTO();

        dto.setId(record.getId());
        dto.setAmount(record.getAmount());
        dto.setType(record.getType());
        dto.setCategory(record.getCategory());
//        dto.setDate(record.getRecordDate());

        return dto;
    }

}