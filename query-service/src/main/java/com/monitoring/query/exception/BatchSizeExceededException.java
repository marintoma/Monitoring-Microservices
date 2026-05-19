package com.monitoring.query.exception;

public class BatchSizeExceededException extends RuntimeException {

    public BatchSizeExceededException(int actual, int max) {
        super("Batch size " + actual + " exceeds maximum allowed size of " + max);
    }
}
