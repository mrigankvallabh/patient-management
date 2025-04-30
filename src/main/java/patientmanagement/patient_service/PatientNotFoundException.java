package patientmanagement.patient_service;

class PatientNotFoundException extends RuntimeException {
    PatientNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
