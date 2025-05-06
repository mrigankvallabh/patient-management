package patientmanagement.patient_service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient", description = "Patient Management API")
class PatientController {
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get All Patients")
    private ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get a Patient by Email")
    private ResponseEntity<PatientResponseDTO> getPatientByEmail(@RequestParam @Email String email) {
        return patientService
                .getPatientByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get a Patient by Id")
    private ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable UUID patientId) {
        return patientService
                .getPatientById(patientId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    private ResponseEntity<PatientResponseDTO> createPatient(
            @RequestBody @Validated PatientRequestDTO patientRequestDTO,
            UriComponentsBuilder ucb) {
        log.info("Creating a new patient with email: {}", patientRequestDTO.email());
        var patient = patientService.createPatient(patientRequestDTO);
        var location = ucb
                .path("/api/v1/patients/{patientId}")
                .buildAndExpand(patient.id())
                .toUri();
        return ResponseEntity.created(location).body(patient);
    }

    @PutMapping("/{patientId}")
    @Operation(summary = "Update a Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID patientId,
            @Validated @RequestBody PatientRequestDTO updatePatientRequest) {
        log.info("Uptate request {}", updatePatientRequest);
        return ResponseEntity.ok(patientService.updatePatient(patientId, updatePatientRequest));
    }

    @DeleteMapping("/{patientId}")
    @Operation(summary = "Delete a Patient")
    private ResponseEntity<Void> deletePatient(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build();
    }

}
