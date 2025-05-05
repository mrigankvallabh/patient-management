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

    Optional<PatientResponseDTO> getPatientById(UUID patientId) {
        return patientRepository.findById(patientId).map(Patient::toResponseDTO);
    }

    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new PatientEmailAlreadyExists(patientRequestDTO.email());
        }
        var patient = new Patient(
                patientRequestDTO.name(),
                patientRequestDTO.email(),
                patientRequestDTO.address(),
                patientRequestDTO.dateOfBirth(),
                patientRequestDTO.dateOfRegistration());
        var savedPatient = patientRepository.save(patient);

        return savedPatient.toResponseDTO();
    }

    PatientResponseDTO updatePatient(UUID patientId, PatientRequestDTO updatePatientRequest) {
        var patient = patientRepository
                .findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId.toString()));
        if (patientRepository.existsByEmailAndIdNot(updatePatientRequest.email(), patient.getId())) {
            throw new PatientEmailAlreadyExists(updatePatientRequest.email());
        }
        patient.setEmail(updatePatientRequest.email());
        patient.setAddress(updatePatientRequest.address());
        return patientRepository.save(patient).toResponseDTO();
    }

    void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

}
