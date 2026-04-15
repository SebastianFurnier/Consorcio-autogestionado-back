package com.tpgdb.Consorcio.Exception;

public record FieldError (
    String field,
    String message
) {}
