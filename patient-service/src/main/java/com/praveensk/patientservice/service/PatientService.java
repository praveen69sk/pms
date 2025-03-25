package com.praveensk.patientservice.service;

import com.praveensk.patientservice.Mapper.PatientMapper;
import com.praveensk.patientservice.dto.PatientRequestDto;
import com.praveensk.patientservice.dto.PatientResponseDto;
import com.praveensk.patientservice.exception.EmailAlreadyExistsException;
import com.praveensk.patientservice.exception.PatientNotFoundException;
import com.praveensk.patientservice.grpc.BillingServiceGrpcClient;
import com.praveensk.patientservice.model.Patient;
import com.praveensk.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A Patient with this email " + patientRequestDto.getEmail() + " already exists");
        }

        Patient patient = patientRepository.save(PatientMapper.toEntity(patientRequestDto));

        billingServiceGrpcClient.craeteBilling(patient.getId().toString(), patient.getName(), patient.getEmail());

        return PatientMapper.toDto(patient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A Patient with this email " + patientRequestDto.getEmail() + " already exists");
        }

        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        Patient upadatedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(upadatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
