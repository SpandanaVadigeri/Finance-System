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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  Service layer for dashboard and analytics operations.

  Purpose:
  - Provides aggregated financial data for dashboard visualization
  - Implements role-based data filtering for VIEWER, ANALYST, and ADMIN roles
  - Handles summary calculations, category breakdowns, trends, and recent activity

  Role-Based Access Summary:
  - VIEWER: Only sees their own aggregated data and transactions
  - ANALYST: Sees all users' data (read-only) for company-wide analysis
  - ADMIN: Same as ANALYST with additional write capabilities (handled elsewhere)

  Key Features:
  - Personal dashboard summaries for individual users
  - Company-wide analytics for management and analysis
  - Category-wise spending and income breakdowns
  - Monthly trend analysis for financial patterns
  - Recent activity feed for quick insights

 */

@Service
public class DashboardService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;


    /*
      Retrieves financial summary for all users combined.

      This method calculates total income, total expense, and net balance
      across ALL financial records in the system without any user filtering.

      Access: All authenticated users (VIEWER, ANALYST, ADMIN) can access

      Calculation Process:
      - Fetches all records from database
      - Iterates through each record to sum INCOME amounts
      - Iterates through each record to sum EXPENSE amounts
      - Calculates net balance as income minus expense

      @return DashboardSummaryDTO containing totalIncome, totalExpense, and netBalance
     */
    public DashboardSummaryDTO getSummary(String email) {

        // Get current user from database
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = currentUser.getRole().getName();
        List<FinancialRecord> records;

        // Role-based data fetching
        if (role.equals("VIEWER")) {
            // VIEWER sees ONLY their own records
            records = recordRepository.findByUserIdAndDeletedFalse(currentUser.getId());
        } else {
            // ANALYST & ADMIN see ALL records
            records = recordRepository.findByDeletedFalse();
        }

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

    /*
      Retrieves category-wise breakdown of all financial records.

      This method groups all transactions by category and calculates
      the total amount spent or earned in each category.

      Access: All authenticated users (VIEWER, ANALYST, ADMIN)

      Processing Logic:
      - Fetches all records from database
      - Uses HashMap to aggregate amounts by category
      - Handles null categories by defaulting to "OTHER"
      - Converts map entries to CategorySummaryDTO list

      @return List of CategorySummaryDTO with category names and total amounts
     */


    public List<CategorySummaryDTO> getCategorySummary(String email) {

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = currentUser.getRole().getName();
        List<FinancialRecord> records;

        // Role-based data fetching
        if (role.equals("VIEWER")) {
            // VIEWER sees ONLY their own records
            records = recordRepository.findByUserIdAndDeletedFalse(currentUser.getId());
        } else {
            // ANALYST & ADMIN see ALL records
            records = recordRepository.findByDeletedFalse();
        }

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

    /*
      Retrieves role-appropriate dashboard data for the authenticated user.

      This method provides different data based on user role:
      - VIEWER: Returns their own personal summary
      - ANALYST/ADMIN: Returns summaries for all VIEWER users in the system

      Access: All authenticated users

      @param email - Email of the authenticated user
      @return Object containing either personal summary or list of all viewers' summaries
      @throws RuntimeException if user not found in database
     */
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

    /*
      Retrieves company-wide financial summary for ANALYST and ADMIN roles.

      This method returns aggregated financial data across all users.
      VIEWER role is explicitly denied access to this endpoint.

      Access: ANALYST and ADMIN only

      @param email Email of the authenticated user
      @return Map containing totalIncome, totalExpense, and netBalance for entire company
      @throws RuntimeException if user is VIEWER or user not found
     */

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

    /*
      Helper method to calculate summary from a list of financial records.

      This internal method performs the core calculation logic used by
      multiple dashboard methods to avoid code duplication.

      @param records -  List of FinancialRecord objects to analyze
      @return Map containing totalIncome, totalExpense, and netBalance keys
     */

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

    /*
      Retrieves monthly financial trends for analysis.

      This method groups transactions by month and calculates total amounts
      to identify spending and income patterns over time.

      Access:
      - VIEWER: Returns their own monthly trends
      - ANALYST/ADMIN: Returns company-wide monthly trends

      @param email - Email of the authenticated user
      @return Map with month names as keys and total amounts as values
     */

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
            String month = r.getRecordDate().getMonth().toString();

            trends.put(month,
                    trends.getOrDefault(month, 0.0) + r.getAmount());
        }

        return trends;
    }

    /*
      Retrieves detailed category-wise financial analysis.

      This method provides comprehensive breakdown of transactions by category
      to help identify major spending areas and income sources.

      Access:
      - VIEWER: Returns their own category analysis
      - ANALYST/ADMIN: Returns company-wide category analysis

      @param email - Email of the authenticated user
      @return Map with category names as keys and total amounts as values
     */

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

    /*
      Retrieves most recent transactions for activity feed.

      This method returns the latest 5 transactions sorted by date
      to provide a quick view of recent financial activity.

      Access:
      - VIEWER: Returns their own recent transactions
      - ANALYST/ADMIN: Returns all users' recent transactions

      Processing Steps:
      - Fetches appropriate records based on user role
      - Sorts records by date in descending order (newest first)
      - Limits results to top 5 transactions
      - Converts each record to response DTO

      @param email Email of the authenticated user
      @return List of FinancialRecordResponseDTO for the 5 most recent transactions
     */

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

    /*
      Helper method to convert FinancialRecord entity to Response DTO.

      This internal method ensures consistent data transformation
      and excludes sensitive or unnecessary fields.

      @param record FinancialRecord entity to convert
      @return FinancialRecordResponseDTO with selected fields
     */
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