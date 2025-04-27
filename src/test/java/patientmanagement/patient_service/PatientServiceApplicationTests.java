package patientmanagement.patient_service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PatientServiceApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldGetAllPatients() {
		var response = restTemplate.getForEntity("/api/v1/patients", PatientResponseDTO[].class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@SuppressWarnings("null") // This is fine as we are already asserted jane is not null
	@Test
	void shouldGetPatientByEmail() {
		String email = "jane.smith@example.com";
		var response = restTemplate.getForEntity("/api/v1/patients/" + email, PatientResponseDTO.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		PatientResponseDTO jane = response.getBody();
		assertThat(jane).isNotNull();
		assertThat(jane.email()).isEqualTo(email);
	}

	/*
	 * @Test
	 * 
	 * @DirtiesContext
	 * void shouldCreateANewPatient() {
	 * var newPatientRequestDto = new PatientRequestDTO(
	 * "Blue Sayama",
	 * "blue.sayama@example.com",
	 * "112 Eager St., Chelmsford",
	 * LocalDate.of(1992, 9, 26),
	 * LocalDate.of(2024, 11, 11));
	 * 
	 * var httpHeaders = new HttpHeaders();
	 * httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	 * ;
	 * var httpRequest = new HttpEntity<>(newPatientRequestDto, httpHeaders);
	 * var createResponse = restTemplate.postForEntity(
	 * "/api/v1/patients",
	 * httpRequest,
	 * PatientResponseDTO.class);
	 * assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	 * }
	 */
}
