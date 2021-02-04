package com.talkdesk.pnia.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AggregatorServiceException extends Exception {

    public AggregatorServiceException(final String errorMessage, Throwable e) throws AggregatorServiceException {
        super(errorMessage, e);
        log.error(errorMessage);
    }
}
