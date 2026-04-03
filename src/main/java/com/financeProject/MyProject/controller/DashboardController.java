package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.CategorySummaryDTO;
import com.financeProject.MyProject.dto.DashboardSummaryDTO;
import com.financeProject.MyProject.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dashboard") // Base path for dashboard APIs
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // GET SUMMARY
    // Returns: total income, total expense, net balance
    // API: GET /dashboard/summary
    @GetMapping("/summary")
    public DashboardSummaryDTO getSummary() {

        // Calls service layer to compute summary
        return dashboardService.getSummary();
    }

    // GET CATEGORY-WISE TOTALS
    // Returns: total amount grouped by category
    // API: GET /dashboard/category
    @GetMapping("/category")
    public List<CategorySummaryDTO> getCategorySummary() {

        // Calls service layer to compute category-wise totals
        return dashboardService.getCategorySummary();
    }
}