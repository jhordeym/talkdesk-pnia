package com.talkdesk.pnia.service;

import com.talkdesk.pnia.api.AggregatorService;
import com.talkdesk.pnia.dto.BusinessSectorResponse;
import com.talkdesk.pnia.exception.AggregatorServiceException;
import com.talkdesk.pnia.util.PhoneNumberUtils;
import com.talkdesk.pnia.webclient.BusinessSectorAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {

    public final BusinessSectorAPI businessSectorAPI;
    public final PrefixReader prefixReader;

    public Map<String, Map<String, Long>> aggregate(final List<String> numbers) throws AggregatorServiceException {
        final Set<BusinessSectorResponse> sectorsForNumbers = getSectorsForNumbers(numbers);
        log.debug("sectorForNumbers: {}", sectorsForNumbers);
        final Map<String, Map<String, Long>> aggregationResult = new TreeMap<>();
        prefixReader.readPrefixesAndApply(prefix -> {
            final Map<String, Long> occurrences = getOccurrences(prefix, sectorsForNumbers);
            if (!occurrences.isEmpty()) {
                aggregationResult.put(prefix, occurrences);
            }
        });
        return aggregationResult;
    }

    private Set<BusinessSectorResponse> getSectorsForNumbers(final List<String> numbers) {
        return numbers.parallelStream()
                .filter(PhoneNumberUtils::isValidNumber)
                .map(n -> {
                    final ResponseEntity<BusinessSectorResponse> response = businessSectorAPI.getSector(n);
                    return HttpStatus.OK == response.getStatusCode() ? response.getBody() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Map<String, Long> getOccurrences(final String prefix, final Set<BusinessSectorResponse> sectorsForNumbers) {
        return sectorsForNumbers.parallelStream()
                .filter(s4n -> Objects.requireNonNull(PhoneNumberUtils.extractActualNumber(s4n.getNumber())).startsWith(prefix))
                .collect(Collectors.groupingBy(BusinessSectorResponse::getSector, Collectors.counting()));
    }
}
