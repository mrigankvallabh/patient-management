package patientmanagement.patient_service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email); // Check if an email already exists in the database

    Optional<Patient> findByEmail(String email); // Find a patient by email

    // Check if an email already exists in the database for a different patient
    boolean existsByEmailAndIdNot(String email, UUID id);
}
