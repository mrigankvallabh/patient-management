package patientmanagement.patient_service;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final UUID id;

    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String address;

    @NotNull
    private final LocalDate dateOfBirth;

    @NotNull
    private final LocalDate dateOfRegistration;

    @Deprecated
    protected Patient() {
        this.id = null;
        this.name = null;
        this.email = null;
        this.address = null;
        this.dateOfBirth = null;
        this.dateOfRegistration = null;
    } // for JPA

    Patient(String name, String email, String address, LocalDate dateOfBirth, LocalDate dateOfRegistration) {
        this.id = null;
        this.name = name;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.dateOfRegistration = dateOfRegistration;
    }

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getEmail() {
        return email;
    }

    void setEmail(@NotNull @Email String email) {
        this.email = email;
    }

    String getAddress() {
        return address;
    }

    void setAddress(@NotBlank String address) {
        this.address = address;
    }

    LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    PatientResponseDTO toResponseDTO() {
        return new PatientResponseDTO(
                id,
                name,
                email,
                address,
                dateOfBirth,
                dateOfRegistration);
    }
}
