package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.CategorySummaryDTO;
import com.financeProject.MyProject.dto.DashboardSummaryDTO;
import com.financeProject.MyProject.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @GetMapping
    public Object getDashboard(java.security.Principal principal) {

        String email = principal.getName();

        return dashboardService.getDashboard(email);
    }

    @GetMapping("/company-summary")
    public Object getCompanySummary(java.security.Principal principal) {

        String email = principal.getName();

        return dashboardService.getCompanySummary(email);
    }

    @GetMapping("/trends")
    public Object getTrends(Principal principal) {
        return dashboardService.getTrends(principal.getName());
    }

    @GetMapping("/category-analysis")
    public Object getCategoryAnalysis(Principal principal) {
        return dashboardService.getCategoryAnalysis(principal.getName());
    }

    @GetMapping("/recent-activity")
    public Object getRecentActivity(Principal principal) {
        return dashboardService.getRecentActivity(principal.getName());
    }



}