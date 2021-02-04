package com.talkdesk.pnia.api;

import com.talkdesk.pnia.exception.AggregatorServiceException;

import java.util.List;
import java.util.Map;

public interface AggregatorService {
    Map<String, Map<String, Long>> aggregate(final List<String> numbers) throws AggregatorServiceException;
}
