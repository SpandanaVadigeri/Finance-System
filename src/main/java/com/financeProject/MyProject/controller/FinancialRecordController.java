package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.FinancialRecordRequestDTO;
import com.financeProject.MyProject.dto.FinancialRecordResponseDTO;
import com.financeProject.MyProject.service.FinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/records")
public class FinancialRecordController {

    @Autowired
    private FinancialRecordService recordService;

    // Create Record (ADMIN only handled in service)
    // POST /records?userId=1&role=ADMIN
    @PostMapping
    public FinancialRecordResponseDTO createRecord(
            @RequestParam Long userId,   // 👈 target user
            @RequestBody FinancialRecordRequestDTO requestDTO,
            Principal principal) {

        String adminEmail = principal.getName();

        return recordService.createRecord(adminEmail, userId, requestDTO);
    }

//    //  Get All Records
//    // GET /records


    @GetMapping("/{id}")
    public FinancialRecordResponseDTO getRecordById(@PathVariable Long id,
                                                    java.security.Principal principal) {

        String email = principal.getName();

        return recordService.getRecordById(id, email);
    }

    // DELETE /records → delete all records
    @DeleteMapping
    public String deleteAllRecords(java.security.Principal principal) {

        String email = principal.getName();

        recordService.deleteAllRecords(email);

        return "All records deleted";
    }

    // GET /records → normal role-based fetch
    @GetMapping("/all")
    public List<FinancialRecordResponseDTO> getAllRecords(Principal principal) {

        String email = principal.getName();

        return recordService.getRecords(email);
    }

    // DELETE /records/{id}
    @DeleteMapping("/{id}")
    public String deleteRecordById(@PathVariable Long id,
                                   java.security.Principal principal) {

        String email = principal.getName();

        recordService.deleteRecordById(id, email);

        return "Record deleted";
    }

    @GetMapping("/filter")
    public List<FinancialRecordResponseDTO> getFilteredRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Principal principal) {

        String email = principal.getName();

        return recordService.getFilteredRecords(email, type, category, startDate, endDate);
    }
    @GetMapping("/user/{userId}")
    public List<FinancialRecordResponseDTO> getRecordsByUserId(
            @PathVariable Long userId,
            java.security.Principal principal) {

        String email = principal.getName();

        return recordService.getRecordsByUserId(userId, email);
    }

    @PutMapping("/{id}")
    public FinancialRecordResponseDTO updateRecord(
            @PathVariable Long id,
            @RequestBody FinancialRecordRequestDTO dto,
            Principal principal) {

        return recordService.updateRecord(id, dto, principal.getName());
    }
}
