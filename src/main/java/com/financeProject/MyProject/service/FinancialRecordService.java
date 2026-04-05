package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.FinancialRecordRequestDTO;
import com.financeProject.MyProject.dto.FinancialRecordResponseDTO;
import com.financeProject.MyProject.model.FinancialRecord;
import com.financeProject.MyProject.model.User;
import com.financeProject.MyProject.repository.FinancialRecordRepository;
import com.financeProject.MyProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public FinancialRecordResponseDTO createRecord(String adminEmail,
                                                   Long targetUserId,
                                                   FinancialRecordRequestDTO dto) {

        //  who is making request
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // only ADMIN allowed
        if (!admin.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can create records");
        }

        // target user
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        FinancialRecord record = new FinancialRecord();

        record.setUser(targetUser);
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setRecordDate(dto.getRecordDate());
        record.setNotes(dto.getNotes());

        return convertToDTO(recordRepository.save(record));
    }



    public List<FinancialRecordResponseDTO> getRecords(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getName();

        List<FinancialRecord> records;

        System.out.println("EMAIL: " + email);
        System.out.println("ROLE: " + role);
        System.out.println("USER ID: " + user.getId());

        if (role.equals("VIEWER")) {
            // Only own records
//            records = recordRepository.findByUserId(user.getId());
              records = recordRepository.findByUserIdAndDeletedFalse(user.getId());
        } else {
            // ANALYST / ADMIN → all records
//            records = recordRepository.findAll();
              records = recordRepository.findByDeletedFalse();
        }

        return records.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public FinancialRecordResponseDTO getRecordById(Long recordId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String role = user.getRole().getName();

        //  VIEWER restriction
        if (role.equals("VIEWER") && !record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToDTO(record);
    }

    public void deleteAllRecords(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN allowed
        if (!user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can delete all records");
        }

//        recordRepository.deleteAll();

        List<FinancialRecord> records = recordRepository.findByDeletedFalse();

        for (FinancialRecord r : records) {
            r.setDeleted(true);
        }

        recordRepository.saveAll(records);
    }

    public void deleteRecordById(Long recordId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN allowed
        if (!user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can delete records");
        }

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

//        recordRepository.delete(record);

        record.setDeleted(true);
        recordRepository.save(record);
    }
    public List<FinancialRecordResponseDTO> getFilteredRecords(
            String email,
            String type,
            String category,
            String startDate,
            String endDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getName();

        List<FinancialRecord> records;

        //  ROLE-BASED FETCH (MAIN FIX)
        if (role.equals("VIEWER")) {
//            records = recordRepository.findByUserId(user.getId()); // Only own
            records = recordRepository.findByUserIdAndDeletedFalse(user.getId());
        } else {
            records = recordRepository.findByDeletedFalse(); // ANALYST & ADMIN
        }

        // Apply filters (same as before)

        if (type != null) {
            records = records.stream()
                    .filter(r -> r.getType().equalsIgnoreCase(type))
                    .toList();
        }

        if (category != null) {
            records = records.stream()
                    .filter(r -> r.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            records = records.stream()
                    .filter(r -> !r.getRecordDate().isBefore(start) &&
                            !r.getRecordDate().isAfter(end))
                    .toList();
        }

        return records.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<FinancialRecordResponseDTO> getRecordsByUserId(Long userId, String email) {

        User currentUser = userRepository.findByEmail(email).orElseThrow();

        String role = currentUser.getRole().getName();

        // VIEWER → only own data
        if (role.equals("VIEWER") && !currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        List<FinancialRecord> records = recordRepository.findByUserIdAndDeletedFalse(userId);

        return records.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public FinancialRecordResponseDTO updateRecord(Long id,
                                                   FinancialRecordRequestDTO dto,
                                                   String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        if (!user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can update records");
        }

        FinancialRecord record = recordRepository.findById(id).orElseThrow();

        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setRecordDate(dto.getRecordDate());
        record.setNotes(dto.getNotes());

        return convertToDTO(recordRepository.save(record));
    }

    public List<FinancialRecordResponseDTO> getRecordsPaginated(
            String email, int page, int size) {

        User user = userRepository.findByEmail(email).orElseThrow();

        String role = user.getRole().getName();

        Pageable pageable = PageRequest.of(page, size);

        Page<FinancialRecord> recordsPage;

        if (role.equals("VIEWER")) {
            recordsPage = recordRepository
                    .findByUserIdAndDeletedFalse(user.getId(), pageable);
        } else {
            recordsPage = recordRepository
                    .findByDeletedFalse(pageable);
        }

        return recordsPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
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

