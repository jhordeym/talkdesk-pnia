package com.talkdesk.pnia.service;

import com.talkdesk.pnia.exception.AggregatorServiceException;
import com.talkdesk.pnia.util.PhoneNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Component
public class PrefixReader {

    @Value("${app.prefixes.fileName}")
    private String filePath;

    public void readPrefixesAndApply(final Consumer<String> consumer) throws AggregatorServiceException {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
            br.lines().parallel().filter(PhoneNumberUtils::isValidPrefix).forEach(consumer::accept);
        } catch (final IOException e) {
            throw new AggregatorServiceException("Couldn't read file: " + filePath, e);
        }
    }
}
