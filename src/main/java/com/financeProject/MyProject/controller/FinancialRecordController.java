package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.FinancialRecordRequestDTO;
import com.financeProject.MyProject.dto.FinancialRecordResponseDTO;
import com.financeProject.MyProject.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class FinancialRecordController {

    @Autowired
    private FinancialRecordService recordService;

    // 🔹 Create Record (ADMIN only handled in service)
    // POST /records?userId=1&role=ADMIN
    @PostMapping
    public FinancialRecordResponseDTO createRecord(
            @RequestParam Long userId,
            @RequestParam String role,
            @RequestBody FinancialRecordRequestDTO requestDTO) {

        return recordService.createRecord(userId, requestDTO, role);
    }

    // 🔹 Get All Records
    // GET /records
    @GetMapping
    public List<FinancialRecordResponseDTO> getAllRecords() {
        return recordService.getAllRecords();
    }
}
