package patientmanagement.patient_service;

import static patientmanagement.patient_service.PatientsConstants.ADDRESS_BLANK;
import static patientmanagement.patient_service.PatientsConstants.DOB_REQUIRED;
import static patientmanagement.patient_service.PatientsConstants.NAME_BLANK;
import static patientmanagement.patient_service.PatientsConstants.NAME_LENGTH;
import static patientmanagement.patient_service.PatientsConstants.NAME_SIZE;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record PatientRequestDTO(
        @NotBlank(message = NAME_BLANK) @Size(max = NAME_LENGTH, message = NAME_SIZE) String name,
        @Email(message = PatientsConstants.EMAIL_INVALID) String email,
        @NotBlank(message = ADDRESS_BLANK) String address,
        @NotNull(message = DOB_REQUIRED) @JsonFormat(shape = Shape.STRING, pattern = YYYY_MM_DD) LocalDate dateOfBirth,
        @JsonFormat(shape = Shape.STRING, pattern = YYYY_MM_DD) LocalDate dateOfRegistration) {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    PatientRequestDTO {

        if (dateOfRegistration == null) {
            dateOfRegistration = LocalDate.now();
        }

    }
}
