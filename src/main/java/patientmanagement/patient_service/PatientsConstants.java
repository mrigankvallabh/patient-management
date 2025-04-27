package patientmanagement.patient_service;

class PatientsConstants {
    static final String NAME_BLANK = "Name must not be blank";
    static final int NAME_LENGTH = 128;
    static final String NAME_SIZE = "Name can have maximum" + NAME_LENGTH + " chars";
    static final String EMAIL_INVALID = "email is invalid";
    static final String ADDRESS_BLANK = "Address must not be blank";
    static final String DOB_REQUIRED = "Date of Birth is required";

    private PatientsConstants() { // cannot instantiate
    }
}
