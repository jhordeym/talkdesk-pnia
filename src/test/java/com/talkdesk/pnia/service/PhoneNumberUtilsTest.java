package com.talkdesk.pnia.service;

import com.talkdesk.pnia.util.PhoneNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class PhoneNumberUtilsTest {

    private static final Map<String, Boolean> expectedMap;

    static {
        expectedMap = new HashMap<>();
        expectedMap.put("00123", true); // min digits with 00
        expectedMap.put("+123", true); // min digits with +
        expectedMap.put("+123A", false); // wrong case because A
        expectedMap.put("+1234", false); // invalid range 4 / {3} or {6,13}
        expectedMap.put("+ 123", false); // invalid space
        expectedMap.put("+123456", true); // min digits of 2nd range {6,13}
        expectedMap.put("+123 456", true); // min digits of 2nd range with spaces
        expectedMap.put("+1234567890123", true); // max digits of 2nd range {6,13}
        expectedMap.put("+123 45 67 89 01 23", true); // max digits of 2nd range with spaces
    }

    @Test
    void isNumberValid() {
        final Map<String, Boolean> actualMap = expectedMap.keySet()
                .stream().collect(Collectors.toMap(n -> n, n -> PhoneNumberUtils.isValidNumber(n)));

        actualMap.entrySet().forEach(e -> log.info("number: {}, expected: {}, actual: {}",
                e.getKey(), expectedMap.get(e.getKey()), e.getValue())
        );

        assertTrue(actualMap.equals(expectedMap));
    }

    @Test
    void testExtractActualNumber() {
        expectedMap.keySet().stream()
                .filter(PhoneNumberUtils::isValidNumber)
                .forEach(n -> {
                    final String actualNumber = PhoneNumberUtils.extractActualNumber(n);
                    log.info("inputNumber: {}, actualNumber: {}", n, actualNumber);
                    assertTrue(Objects.nonNull(actualNumber));
                });
    }
}
