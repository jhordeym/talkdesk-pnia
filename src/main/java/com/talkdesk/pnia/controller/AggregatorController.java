package com.talkdesk.pnia.controller;

import com.talkdesk.pnia.api.AggregatorService;
import com.talkdesk.pnia.exception.AggregatorServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AggregatorController {

    private final AggregatorService aggregatorService;

    @PostMapping("/aggregate")
    public ResponseEntity<?> aggregate(@RequestBody final List<String> phoneNumbers) {
        try {
            return ResponseEntity.ok(aggregatorService.aggregate(phoneNumbers));
        } catch (final AggregatorServiceException ase) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ase.getMessage());
        }
    }
}
