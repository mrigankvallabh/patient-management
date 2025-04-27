package patientmanagement.patient_service;

import java.time.LocalDate;
import java.util.UUID;

record PatientResponseDTO(
                UUID id,
                String name,
                String email,
                String address,
                LocalDate dateOfBirth,
                LocalDate dateOfRegistration) {
}
