package com.talkdesk.pnia.service;

import com.talkdesk.pnia.exception.AggregatorServiceException;
import com.talkdesk.pnia.util.PhoneNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PrefixReader {

    @Value("${app.prefixes.fileName}")
    private String filePath;

    private Set<String> cachedPrefixes;

    @PostConstruct
    public void readPrefixesAndCache() throws AggregatorServiceException {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
            cachedPrefixes = br.lines().filter(PhoneNumberUtils::isValidPrefix).collect(Collectors.toSet());
        } catch (final IOException e) {
            throw new AggregatorServiceException("Couldn't read file: " + filePath, e);
        }
    }

    @PreDestroy
    public void clearCache() {
        if (Objects.nonNull(cachedPrefixes)) cachedPrefixes = null;
    }

    public void readPrefixesAndApply(final Consumer<String> consumer) {
        if (Objects.nonNull(cachedPrefixes)) cachedPrefixes.parallelStream().forEach(consumer);
    }
}
