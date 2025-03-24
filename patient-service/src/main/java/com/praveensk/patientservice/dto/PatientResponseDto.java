package com.praveensk.patientservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientResponseDto {

    private String id;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
}
