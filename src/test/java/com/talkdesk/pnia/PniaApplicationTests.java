package com.talkdesk.pnia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkdesk.pnia.exception.AggregatorServiceException;
import com.talkdesk.pnia.service.PrefixReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class PniaApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrefixReader prefixReader;

    @Test
    void contextLoads() {
    }

    @Test
    void givenPhoneNumbers_whenAggregate_thenStatus200() throws Exception {
        final List<String> requestBody = Arrays.asList("+1983248", "001382355", "+147 8192", "+4439877");
        mvc.perform(
                post("/aggregate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void prefixReaderIntegrationTest() throws AggregatorServiceException {
        final AtomicLong amountOfPrefixes = new AtomicLong();
        Assertions.assertDoesNotThrow(() -> prefixReader.readPrefixesAndApply(prefix -> {
            if (Objects.nonNull(prefix)) {
                amountOfPrefixes.getAndIncrement();
            }
        }));
        log.info("AmountOfPrefixes: {}", amountOfPrefixes);
        assertTrue(amountOfPrefixes.get() > 1);
    }

}
