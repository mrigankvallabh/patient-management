package patientmanagement.patient_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContentAssert;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

@JsonTest
class PatientServiceJsonTests {
    @Autowired
    private JacksonTester<PatientRequestDTO> patientRequestJson;
    private PatientRequestDTO requestDTO;
    private PatientRequestDTO requestDTOWithNullRegistrationDate;

    @Autowired
    private JacksonTester<PatientResponseDTO[]> patientListResponseJson;
    private PatientResponseDTO[] responseDTOs;

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        requestDTO = new PatientRequestDTO(
                "Blue Sayama",
                "blue.sayama@example.com",
                "112 Mobin St., Crowsand",
                LocalDate.of(1992, 9, 15),
                LocalDate.of(2024, 11, 7));
        requestDTOWithNullRegistrationDate = new PatientRequestDTO(
                "Blue Sayama",
                "blue.sayama@example.com",
                "112 Mobin St., Crowsand",
                LocalDate.of(1992, 9, 15),
                null);

        responseDTOs = Arrays.array(
                new PatientResponseDTO(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        "Blue Sayama",
                        "blue.sayama@example.com",
                        "112 Mobin St., Crowsand",
                        LocalDate.of(1992, 9, 15),
                        LocalDate.of(2024, 11, 7)),
                new PatientResponseDTO(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        "Akane Wilson",
                        "akane.wilson@example.com",
                        "358 Mobin St., Crowsand",
                        LocalDate.of(1991, 1, 5),
                        LocalDate.of(2024, 1, 17)),
                new PatientResponseDTO(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        "Nema Tali",
                        "nema.tali@example.com",
                        "1321 Dryer Road, Shervil",
                        LocalDate.of(1995, 11, 18),
                        LocalDate.of(2023, 3, 18)),
                new PatientResponseDTO(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        "Ivanka Shrek",
                        "ivanka.s@example.com",
                        "34 Mulberry Av., Bloomington",
                        LocalDate.of(1995, 5, 25),
                        LocalDate.of(2024, 8, 28)));

    }

    @Test
    void patientRequestSerializationTest() throws IOException {
        var jsonRequest = patientRequestJson.write(requestDTO);

        JsonContentAssert assertObject = assertThat(jsonRequest);
        assertObject.isStrictlyEqualToJson("request.json");
        assertObject.hasJsonPathStringValue("@.name");
        assertObject.extractingJsonPathStringValue("@.name").isEqualTo("Blue Sayama");

        assertObject.hasJsonPathStringValue("@.email");
        assertObject.extractingJsonPathStringValue("@.email").isEqualTo("blue.sayama@example.com");

        assertObject.hasJsonPathStringValue("@.address");
        assertObject.extractingJsonPathStringValue("@.address").isEqualTo("112 Mobin St., Crowsand");

        assertObject.hasJsonPathStringValue("@.dateOfBirth");
        assertObject.extractingJsonPathStringValue("@.dateOfBirth").isEqualTo("1992-09-15");

        assertObject.hasJsonPathStringValue("@.dateOfRegistration");
        assertObject.extractingJsonPathStringValue("@.dateOfRegistration").isEqualTo("2024-11-07");
    }

    @Test
    void patientRequestWithNullRegistrationDateSerializationTest() throws IOException {
        var jsonRequest = patientRequestJson.write(requestDTOWithNullRegistrationDate);

        JsonContentAssert assertObject = assertThat(jsonRequest);

        assertObject.hasJsonPathStringValue("@.name");
        assertObject.extractingJsonPathStringValue("@.name").isEqualTo("Blue Sayama");

        assertObject.hasJsonPathStringValue("@.email");
        assertObject.extractingJsonPathStringValue("@.email").isEqualTo("blue.sayama@example.com");

        assertObject.hasJsonPathStringValue("@.address");
        assertObject.extractingJsonPathStringValue("@.address").isEqualTo("112 Mobin St., Crowsand");

        assertObject.hasJsonPathStringValue("@.dateOfBirth");
        assertObject.extractingJsonPathStringValue("@.dateOfBirth").isEqualTo("1992-09-15");

        assertObject.hasJsonPathStringValue("@.dateOfRegistration");
        assertObject.extractingJsonPathStringValue("@.dateOfRegistration").isEqualTo(LocalDate.now().toString());
    }

    @Test
    void patientRequestDeserializationTest() throws IOException {
        String stringifiedRequest = """
                {
                  "name": "Blue Sayama",
                  "email": "blue.sayama@example.com",
                  "address": "112 Mobin St., Crowsand",
                  "dateOfBirth": "1992-09-15",
                  "dateOfRegistration": "2024-11-07"
                }
                """;
        var jsonRequest = patientRequestJson.parse(stringifiedRequest);
        var jsonObject = patientRequestJson.parseObject(stringifiedRequest);
        assertThat(jsonRequest).isInstanceOf(PatientRequestDTO.class);
        assertThat(jsonObject).isInstanceOf(PatientRequestDTO.class);
        var expectedObject = requestDTO;
        assertThat(jsonRequest).isEqualTo(expectedObject);
        assertThat(jsonObject).isEqualTo(expectedObject);
    }

    @Test
    void patientRequestWithNullRegistrationDateDeserializationTest() throws IOException {
        String stringifiedRequest = """
                {
                  "name": "Blue Sayama",
                  "email": "blue.sayama@example.com",
                  "address": "112 Mobin St., Crowsand",
                  "dateOfBirth": "1992-09-15"
                }
                """;
        var jsonRequest = patientRequestJson.parse(stringifiedRequest);
        var jsonObject = patientRequestJson.parseObject(stringifiedRequest);
        assertThat(jsonRequest).isInstanceOf(PatientRequestDTO.class);
        assertThat(jsonObject).isInstanceOf(PatientRequestDTO.class);
        var expectedObject = requestDTOWithNullRegistrationDate;
        assertThat(jsonRequest).isEqualTo(expectedObject);
        assertThat(jsonObject).isEqualTo(expectedObject);
    }

    @Test
    void patientResponseSerializationTest() throws IOException {
        var responseListjson = patientListResponseJson.write(responseDTOs);
        assertThat(responseListjson).isStrictlyEqualToJson("responseList.json");
    }

    @Test
    void patientRequestDeserializationShouldFailWithInvalidJSON() throws IOException {
        String invalidRequest = """
                {
                  "name": "    ",
                  "email": "blue.sayama-example.com",
                  "address": "112 Mobin St., Crowsand",
                  "dateOfRegistration": "2024-11-07"
                }
                """;
        var jsonRequest = patientRequestJson.parseObject(invalidRequest);
        var violations = validator.validate(jsonRequest);
        assertThat(violations).hasSize(3);
        assertThat(violations)
                .extracting("message")
                .containsExactlyInAnyOrder(
                        PatientsConstants.EMAIL_INVALID,
                        PatientsConstants.NAME_BLANK,
                        PatientsConstants.DOB_REQUIRED);
    }
}
