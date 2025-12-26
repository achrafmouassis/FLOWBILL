package com.flowbill.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        // Since we are connected to the Tenant DB via Routing DS, we can query tables
        Map<String, Object> stats = new HashMap<>();
        
        Integer projectCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM projects", Integer.class);
        Integer taskCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tasks", Integer.class);
        Integer invoiceCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM invoices", Integer.class);
        
        stats.put("projects", projectCount);
        stats.put("tasks", taskCount);
        stats.put("invoices", invoiceCount);
        
        return stats;
    }
}
