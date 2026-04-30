package com.tpgdb.Consorcio.ExceptionHandler;

import com.tpgdb.Consorcio.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldError(
                        err.getField(),
                        err.getDefaultMessage()))
                .toList();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                System.currentTimeMillis(),
                errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPartnerIDException.class)
    public ResponseEntity<FieldError> handleInvalidPartnerId(
            InvalidPartnerIDException ex) {

        FieldError error = new FieldError("Error", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDataPartnerException.class)
    public ResponseEntity<FieldError> handleInvalidPartnerData(
            InvalidDataPartnerException ex) {

        FieldError error = new FieldError("Error", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<FieldError> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        FieldError error = new FieldError("Error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<FieldError> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {
        FieldError error = new FieldError("Error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidConsorcioException.class)
    public ResponseEntity<FieldError> handleInvalidConsorcio(
            InvalidConsorcioException ex) {
        FieldError error = new FieldError("Error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxPartnerLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxPartnerLimitExceeded(
            MaxPartnerLimitExceededException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

}
