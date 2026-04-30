package com.tpgdb.Consorcio.Exception;

public class MaxPartnerLimitExceededException extends RuntimeException {
    public MaxPartnerLimitExceededException(String message) {
        super(message);
    }
}
