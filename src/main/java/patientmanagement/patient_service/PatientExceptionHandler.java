package patientmanagement.patient_service;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class PatientExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(PatientExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex
                .getBindingResult()
                .getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            PatientEmailAlreadyExists.class,
            PatientNotFoundException.class,
            HttpMessageNotReadableException.class,
            DateTimeParseException.class })
    <T extends RuntimeException> ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(T ex) {
        var errorMessage = switch (ex) {
            case PatientEmailAlreadyExists e -> "Email " + e.getMessage() + " is already in use";
            case PatientNotFoundException e -> "Patient " + e.getMessage() + " NOT FOUND";
            case HttpMessageNotReadableException e -> "JSON Parse Error " + e.getMessage();
            case DateTimeParseException e -> "Bad Date (use YYYY-MM-DD) " + e.getMessage();
            default -> "Bad Request (Reason Not Disclosed)";
        };

        var httpStatus = switch (ex) {
            case PatientNotFoundException e -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };

        log.warn("Error ({}): {}", httpStatus.value(), errorMessage);
        return ResponseEntity.status(httpStatus).body(Map.of("message", errorMessage));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> handleUnexpectedExceptions(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex); // Log full stack trace
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "An internal error occurred"));
    }
}
