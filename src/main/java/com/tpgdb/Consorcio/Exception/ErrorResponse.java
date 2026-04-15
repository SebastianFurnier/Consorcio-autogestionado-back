package com.tpgdb.Consorcio.Exception;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        long timeStamp,
        List<FieldError> errors
) {
}
