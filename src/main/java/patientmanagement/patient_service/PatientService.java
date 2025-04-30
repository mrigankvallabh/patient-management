package patientmanagement.patient_service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
class PatientService {
    private final PatientRepository patientRepository;

    PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    List<PatientResponseDTO> getAllPatients() {
        return patientRepository
                .findAll()
                .stream()
                .map(Patient::toResponseDTO)
                .toList();
    }

    Optional<PatientResponseDTO> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email).map(Patient::toResponseDTO);
    }

    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        var patient = new Patient(
                patientRequestDTO.name(),
                patientRequestDTO.email(),
                patientRequestDTO.address(),
                patientRequestDTO.dateOfBirth(),
                patientRequestDTO.dateOfRegistration());
        var savedPatient = patientRepository.save(patient);

        return savedPatient.toResponseDTO();
    }

    Optional<PatientResponseDTO> updatePatient(String email, PatientRequestDTO updatePatientRequest) {
        return patientRepository
                .findByEmail(email)
                .filter(patient -> !patientRepository
                        .existsByEmailAndIdNot(updatePatientRequest.email(), patient.getId()))
                .map(patient -> {
                    patient.setEmail(updatePatientRequest.email());
                    patient.setAddress(updatePatientRequest.address());
                    return patientRepository.save(patient).toResponseDTO();
                });
    }

    void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

}
