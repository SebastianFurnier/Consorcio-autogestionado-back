package com.tpgdb.Consorcio.ExceptionHandler;

import com.tpgdb.Consorcio.Exception.ErrorResponse;
import com.tpgdb.Consorcio.Exception.FieldError;
import com.tpgdb.Consorcio.Exception.InvalidDataPartnerException;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                        err.getDefaultMessage()
                ))
                .toList();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                System.currentTimeMillis(),
                errors
        );

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

}
