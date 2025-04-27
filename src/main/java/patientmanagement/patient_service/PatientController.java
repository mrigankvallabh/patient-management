package patientmanagement.patient_service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/patients")
class PatientController {
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    private ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{email}")
    private ResponseEntity<PatientResponseDTO> getPatientByEmail(@PathVariable String email) {
        var patient = patientService.getPatientByEmail(email);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<PatientResponseDTO> createPatient(
            @RequestBody @Validated PatientRequestDTO patientRequestDTO,
            UriComponentsBuilder ucb) {
        log.info("Creating a new patient with email: {}", patientRequestDTO.email());
        var patient = patientService.createPatient(patientRequestDTO);
        var location = ucb.path("/api/v1/patients/{email}").buildAndExpand(patient.email()).toUri();
        return ResponseEntity.created(location).body(patient);
    }
}
