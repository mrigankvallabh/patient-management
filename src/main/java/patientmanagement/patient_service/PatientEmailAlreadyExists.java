package patientmanagement.patient_service;

class PatientEmailAlreadyExists extends RuntimeException {
    PatientEmailAlreadyExists(String errorMessage) {
        super(errorMessage);
    }
}
