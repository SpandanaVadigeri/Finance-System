package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.FinancialRecordRequestDTO;
import com.financeProject.MyProject.dto.FinancialRecordResponseDTO;
import com.financeProject.MyProject.model.FinancialRecord;
import com.financeProject.MyProject.model.User;
import com.financeProject.MyProject.repository.FinancialRecordRepository;
import com.financeProject.MyProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    //  CREATE RECORD (Only ADMIN allowed)
    public FinancialRecordResponseDTO createRecord(Long userId,
                                                   FinancialRecordRequestDTO dto,
                                                   String roleName) {

        // Access control
        if (!roleName.equals("ADMIN")) {
            throw new RuntimeException("Access Denied: Only ADMIN can create records");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert DTO → Entity
        FinancialRecord record = new FinancialRecord();
        record.setUser(user);
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setNotes(dto.getNotes());

        // Convert String → LocalDate
        record.setRecordDate(LocalDate.parse(dto.getRecordDate()));

        // Save to DB
        FinancialRecord saved = recordRepository.save(record);

        // Convert Entity → Response DTO
        return convertToDTO(saved);
    }

    //  GET ALL RECORDS (All roles allowed)
    public List<FinancialRecordResponseDTO> getAllRecords() {

        return recordRepository.findAll()
                .stream()
                .map(this::convertToDTO) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    //  HELPER METHOD (Entity → DTO conversion)
    private FinancialRecordResponseDTO convertToDTO(FinancialRecord record) {

        FinancialRecordResponseDTO dto = new FinancialRecordResponseDTO();

        dto.setId(record.getId());
        dto.setAmount(record.getAmount());
        dto.setType(record.getType());
        dto.setCategory(record.getCategory());
        dto.setRecordDate(record.getRecordDate().toString());
        dto.setUserId(record.getUser().getId());

        return dto;
    }
}
