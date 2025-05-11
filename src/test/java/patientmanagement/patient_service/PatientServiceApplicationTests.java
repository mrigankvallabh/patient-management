package patientmanagement.patient_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

/**
 * Important the Tests depend on the data.sql in test/resources
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PatientServiceApplicationTests {

    private static final String API_ROOT_URL = "/api/v1/patients";
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllPatientsWhenListIsRequested() {
        var response = restTemplate.getForEntity(API_ROOT_URL, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        int patientsCount = documentContext.read("$.length()");
        assertThat(patientsCount).isEqualTo(4);
        JSONArray emails = documentContext.read("$..email");
        assertThat(emails).containsExactlyInAnyOrder(
                "alice.johnson@example.com",
                "emily.davis@example.com",
                "james.harris@example.com",
                "isabella.walker@example.com");
    }

    @Test
    void shouldReturnAnExistingPatientByEmail() {
        String requestEmail = "isabella.walker@example.com";
        var response = restTemplate
                .getForEntity(
                        API_ROOT_URL + "/by-email?email=" + requestEmail,
                        String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var isabellaJson = JsonPath.parse(response.getBody());
        String id = isabellaJson.read("$.id");
        var uuid = UUID.fromString(id);
        assertThat(uuid).isEqualTo(UUID.fromString("223e4567-e89b-12d3-a456-426614174014"));
        String email = isabellaJson.read("$.email");
        assertThat(email).isEqualTo(requestEmail);
        String address = isabellaJson.read("$.address");
        assertThat(address).isEqualTo("789 Willow St, Springfield");
        String dob = isabellaJson.read("$.dateOfBirth");
        assertThat(dob).isEqualTo(LocalDate.of(1987, 10, 17).toString());
        String dor = isabellaJson.read("$.dateOfRegistration");
        assertThat(dor).isEqualTo(LocalDate.of(2024, 03, 29).toString());

    }

    @Test
    void shouldNotReturnAnUnknownPatientByEmail() {
        String requestEmail = "secret.stalker@unknown.com";
        var response = restTemplate.getForEntity(
                API_ROOT_URL + "/by-email?email=" + requestEmail, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("null") // already testing Not NULL response body
    void shouldCreateANewPatient() {
        var newPatientRequest = new PatientRequestDTO(
                "Blue Sayama",
                "blue.sayama@example.com",
                "112 Fletcher St., Allsbury",
                LocalDate.of(1996, 6, 18),
                LocalDate.of(2024, 7, 22));
        var createResponse = restTemplate
                .postForEntity(API_ROOT_URL, newPatientRequest, PatientResponseDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        var locationOfNewPatient = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewPatient)
                .isNotNull()
                .hasPath(API_ROOT_URL + "/" + createResponse.getBody().id());

        var getResponse = restTemplate
                .getForEntity(locationOfNewPatient, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var blueJson = JsonPath.parse(getResponse.getBody());
        String id = blueJson.read("$.id");
        assertThatCode(() -> UUID.fromString(id)).doesNotThrowAnyException();
        assertThat(id).isNotNull();
        String email = blueJson.read("$.email");
        assertThat(email).isEqualTo("blue.sayama@example.com");
        String dor = blueJson.read("$.dateOfRegistration");
        assertThat(dor).isEqualTo(LocalDate.of(2024, 7, 22).toString());
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("null") // already testing Not NULL response body
    void shouldCreateANewPatientWhenRegistrationDateNotProvided() {
        var newPatientRequest = new PatientRequestDTO(
                "Blue Sayama",
                "blue.sayama@example.com",
                "112 Fletcher St., Allsbury",
                LocalDate.of(1996, 6, 18),
                null);
        var createResponse = restTemplate
                .postForEntity(API_ROOT_URL, newPatientRequest, PatientResponseDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        var locationOfNewPatient = createResponse.getHeaders().getLocation();
        assertThat(locationOfNewPatient)
                .isNotNull()
                .hasPath(API_ROOT_URL + "/" + createResponse.getBody().id());

        var getResponse = restTemplate
                .getForEntity(locationOfNewPatient, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var blueJson = JsonPath.parse(getResponse.getBody());
        String id = blueJson.read("$.id");
        assertThatCode(() -> UUID.fromString(id)).doesNotThrowAnyException();
        assertThat(id).isNotNull();
        String email = blueJson.read("$.email");
        assertThat(email).isEqualTo("blue.sayama@example.com");
        String dor = blueJson.read("$.dateOfRegistration");
        assertThat(dor).isEqualTo(LocalDate.now().toString());
    }

    @Test
    @SuppressWarnings("null") // already testing Not NULL response body
    void shouldUpdateAnExistingPatient() {
        var emilyDavis = restTemplate
                .getForEntity(
                        API_ROOT_URL + "/by-email?email=emily.davis@example.com",
                        PatientResponseDTO.class)
                .getBody();

        // Ensure that emily.davis@example.com exists in test/resources/data.sql
        assertThat(emilyDavis).isNotNull();
        var updatePatientRequest = new PatientRequestDTO(
                "Emily David",
                "emily.david@example.com",
                "378 Bourbon Garden, Chukla",
                LocalDate.now(),
                LocalDate.now());
        var request = new HttpEntity<>(updatePatientRequest);
        var response = restTemplate
                .exchange(
                        API_ROOT_URL + "/" + emilyDavis.id(),
                        HttpMethod.PUT,
                        request,
                        PatientResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var getResponseAfter = restTemplate
                .getForEntity(API_ROOT_URL + "/" + emilyDavis.id(), String.class);
        assertThat(getResponseAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(getResponseAfter.getBody());
        String address = documentContext.read("$.address");
        assertThat(address).isEqualTo(updatePatientRequest.address());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo(emilyDavis.name());
        // Name, DOB, DOR cannot change
        String dob = documentContext.read("$.dateOfBirth");
        assertThat(LocalDate.parse(dob)).isEqualTo(emilyDavis.dateOfBirth());
        String dor = documentContext.read("$.dateOfRegistration");
        assertThat(LocalDate.parse(dor)).isEqualTo(emilyDavis.dateOfRegistration());
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("null")
    void shouldDeleteAnExistingPatientById() {
        var emilyDavis = restTemplate
                .getForEntity(
                        API_ROOT_URL + "/by-email?email=emily.davis@example.com",
                        PatientResponseDTO.class)
                .getBody();

        // Ensure that emily.davis@example.com exists in test/resources/data.sql
        assertThat(emilyDavis).isNotNull();

        var response = restTemplate
                .exchange(
                        API_ROOT_URL + "/" + emilyDavis.id(),
                        HttpMethod.DELETE,
                        null,
                        Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        var getResponse = restTemplate
                .getForEntity(
                        API_ROOT_URL + "/by-email?email=emily.davis@example.com",
                        String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
