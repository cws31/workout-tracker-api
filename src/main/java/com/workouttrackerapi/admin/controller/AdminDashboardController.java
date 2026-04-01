package com.workouttrackerapi.admin.controller;

import com.workouttrackerapi.admin.dto.AdminDashboardResponse;
import com.workouttrackerapi.admin.service.AdminDashboardService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public AdminDashboardResponse getDashboard() {
        return dashboardService.getDashboardData();
    }
}